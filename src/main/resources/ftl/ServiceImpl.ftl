package ${basePackageName}.service.table.impl;

import cn.bit.bdp.commons.dao.BaseDao;
import cn.bit.bdp.commons.service.AbstractService;
import ${basePackageName}.dao.${shortPackageName}.${domainName}Mapper;
import ${basePackageName}.entity.${shortPackageName}.${domainName};
import ${basePackageName}.entity.${shortPackageName}.${domainName}Example;
import ${basePackageName}.service.${shortPackageName}.${domainName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ${domainName}ServiceImpl extends AbstractService<${domainName}, ${domainName}Example> implements ${domainName}Service {
    @Autowired
    private ${domainName}Mapper ${domainNameVars}Mapper;

    @Override
    protected BaseDao<${domainName}, ${domainName}Example> getBaseMapper() {
        return ${domainNameVars}Mapper;
    }
}
