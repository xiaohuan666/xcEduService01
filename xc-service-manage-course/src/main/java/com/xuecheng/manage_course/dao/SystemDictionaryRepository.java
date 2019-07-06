package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface SystemDictionaryRepository extends MongoRepository<SysDictionary,String> {
    SysDictionary findByDType(String dType);
}
