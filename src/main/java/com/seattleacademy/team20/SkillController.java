package com.seattleacademy.team20;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Controller
//Spring MVCのコントローラーに該当するクラスに付与。
//付与することでSpirngのコンポーネントとして認識され、@Component（や@Repository、@Serviceなど）と同じくApplicationContextに登録され、DI対象のクラスとなる。
//@Componentと基本的な意味合いは同じで、@Controller を付与した場合は @Component はつけない。
public class SkillController {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  //	Spring は、 JdbcTemplate と呼ばれるテンプレートクラスを提供し、
  //	SQL リレーショナルデータベースと JDBC を簡単に操作できるようにする。
  private static final Logger logger = LoggerFactory.getLogger(SkillController.class);

  @RequestMapping(value = "/skillupload", method = RequestMethod.GET)
  public String skillupload(Locale locale, Model model) {
    logger.info("Welcome skill! The client locale is {}.", locale);
    try {
      initialize();
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<SkillInfo> skills = selectSkills();
    uploadSkill(skills);

    return "skill-upload";
  }

  public List<SkillInfo> selectSkills() {
    final String sql = "select * from skills";
    return jdbcTemplate.query(sql, new RowMapper<SkillInfo>() {
      public SkillInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SkillInfo(rs.getString("category"),
            rs.getString("name"), rs.getInt("score"));
      }
    });
  }

  private FirebaseApp app;

  public void initialize() throws IOException {
    FileInputStream refreshToken = new FileInputStream(
        "/Users/hatanyuto/test/dev-yuto-hatano-firebase-adminsdk-bgvtt-416796d4de.json");

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(refreshToken))
        .setDatabaseUrl("https://dev-yuto-hatano.firebaseio.com/")
        .build();
    app = FirebaseApp.initializeApp(options, "other");
  }

  public void uploadSkill(List<SkillInfo> skillList) {
    final FirebaseDatabase database = FirebaseDatabase.getInstance(app);
    DatabaseReference ref = database.getReference("/skillcategory");

    //    複合キーの作成
    //    Function<Skills, String> compositeKey = prd -> {
    //      StringBuffer sb = new StringBuffer();
    //      sb.append(prd.getName()).append("-").append(prd.getScore());
    //      return sb.toString();
    //    };

    //		データの取得
    //    List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
    //    Map<String, Object> map;
    //    Map<String, Map<String, List<Skills>>> skillMap = skillList.stream().collect(
    //        Collectors.groupingBy(Skills::getCategory,
    //            Collectors.groupingBy(compositeKey)));
    //    for (Entry<String, Map<String, List<Skills>>> entry : skillMap.entrySet()) {

    //    List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
    Map<String, Object> map = new HashMap<>();
    Map<String, List<SkillInfo>> skillMap = skillList.stream().collect(Collectors.groupingBy(SkillInfo::getCategory));
    //    Arraylist使うとき、要素数わかってる場合は指定する
    List<Map<String, Object>> list = new ArrayList<>(map.size());
    Map<String, Object> innerMap;

    String[] categories = { "front_end", "back_end", "dev_Ops" };
    for (String category : categories) {
      innerMap = new HashMap<>();
      innerMap.put("category", category);
      innerMap.put("skills", skillMap.get(category).stream().map(s -> s.getSkills()).collect(Collectors.toList()));
      list.add(innerMap);
      //    for (Entry<String, List<SkillInfo>> entry : skillMap.entrySet()) {
      //      map = new HashMap<>();
      //      map.put("category", entry.getKey());
      //      map.put("skills", entry.getValue().stream().map(s -> s.getSkills()).collect(Collectors.toList()));
      //
      //      datalist.add(map);
      //      switch文にbreakは必須！！
      //      switch (entry.getKey()) {
      //      case "front_end":
      //        datalist.add(0, map);
      //        break;
      //      case "back_end":
      //        datalist.add(1, map);
      //        break;
      //      case "dev_Ops":
      //        datalist.add(2, map);
      //        break;

    }

    //				jdbcTemplate.query("select category ,name, score from skills",
    //	            new RowMapper<Skills>() {
    //	                public Skills mapRow(ResultSet rs, int rowNum) throws SQLException {
    //	                    Map<String, Object> map = new HashMap();
    //	                    map.put("category", rs.getString("category");
    //	                    map.put("name", rs.getString("name"));
    //	                    map.put("score", rs.getString("score"));
    //	                    return new Skills(rs.getString("category"),rs.getString("name"),rs.getInt("score"));
    ref.setValue(list, new DatabaseReference.CompletionListener() {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
          System.out.println("data could be saved" + databaseError.getMessage());
        } else {
          System.out.println("Data save successfully.");
        }
      }
    });
  }

  //	データの整形
  //		インスタンス変数の設定
  public class SkillInfo {
    private String category;
    private Skills skills;

    public String getCategory() {
      return category;
    }

    public Skills getSkills() {
      return skills;
    }

    public SkillInfo(String category, String name, int score) {
      this.category = category;
      this.skills = new Skills(name, score);
    }
  }

  public class Skills {
    private String name;
    private int score;

    // 	コンストラクタの準備
    public Skills(String name, int score) {
      this.name = name;
      this.score = score;
    }

    public String getName() {
      return name;
    }

    public int getScore() {
      return score;
    }

  }
}
