package com.seattleacademy.team20;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SkillController{

	private static final Logger logger = LoggerFactory.getLogger(SkillController.class);
	@RequestMapping(value = "/skillupload", method = RequestMethod.GET)
	public String skillupload(Locale locale, Model model) {
        logger.info("Welcome skill! The client locale is {}.", locale);
		return "skill-upload";
	}
}