package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.web.bind.annotation.PathVariable;

public interface SystemDictionaryService {
    public SysDictionary getDictionary(String dType);
}
