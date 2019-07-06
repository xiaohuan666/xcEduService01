package com.xuecheng.manage_course.service.impl;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.dao.SystemDictionaryRepository;
import com.xuecheng.manage_course.service.SystemDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemDictionaryServiceImpl implements SystemDictionaryService {
    @Autowired
    SystemDictionaryRepository systemDictionaryRepository;

    @Override
    public SysDictionary getDictionary(String dType) {
        return   systemDictionaryRepository.findByDType(dType);

    }
}
