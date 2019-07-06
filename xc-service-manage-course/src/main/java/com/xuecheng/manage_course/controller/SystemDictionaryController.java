package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.SystemDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.service.SystemDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sys/dictionary")
public class SystemDictionaryController implements SystemDictionaryControllerApi {
    @Autowired
    SystemDictionaryService systemDictionaryService;

    @Override
    @GetMapping("/get/{dType}")
    @ResponseBody
    public SysDictionary getDictionary(@PathVariable("dType") String dType) {
        System.out.println(dType);
        return systemDictionaryService.getDictionary(dType);
    }
}
