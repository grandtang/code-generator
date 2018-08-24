package cn.tangzy.cg;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tangzhiyuan@bitnei.cn
 * @date 8/22/18
 **/
public class GeneratorMain {
    private static Logger logger = LoggerFactory.getLogger(GeneratorMain.class);

    public static void main(String[] args) throws Exception {
//        generatorByConfig("generatorConfig.xml");

//        generateMysqlByCode("BI_TABLE_INFO",
//                "TableInfo",
//                "/home/tangzy/workspace/github/code-generator",
//                "cn.bit.bdp.cr",
//                "table",
//                "cr",
//                "jdbc:mysql://192.168.6.103:3306/regdb",
//                "regadmin", "MySQL57@csdn.net");
//
//        generateMysqlByCode("BI_MENU_INFO",
//                "MenuInfo",
//                "/home/tangzy/workspace/github/code-generator",
//                "cn.bit.bdp.cr",
//                "menu",
//                "cr",
//                "jdbc:mysql://192.168.6.103:3306/regdb",
//                "regadmin", "MySQL57@csdn.net");
//        generateMysqlByCode("BI_MENU_TABLE_RELATION",
//                "MenuTableRelation",
//                "/home/tangzy/workspace/github/code-generator",
//                "cn.bit.bdp.cr",
//                "relation",
//                "cr",
//                "jdbc:mysql://192.168.6.103:3306/regdb",
//                "regadmin", "MySQL57@csdn.net");
//
//        generateMysqlByCode("BI_NAVIGATION_INFO",
//                "Navigation",
//                "/home/tangzy/workspace/github/code-generator",
//                "cn.bit.bdp.cr",
//                "navigation",
//                "cr",
//                "jdbc:mysql://192.168.6.103:3306/regdb",
//                "regadmin", "MySQL57@csdn.net");


        generateMysqlByCode("user",
                "User",
                "/home/tangzy/workspace/github/code-generator",
                "cn.tangzy.oauth2",
                "user",
                "oauth-server",
                "jdbc:mysql://localhost:3306/test",
                "admin", "root");

    }

    private static void generatorByConfig(String fileName) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        InputStream resourceAsStream = GeneratorMain.class.getClassLoader().getResourceAsStream(fileName);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(resourceAsStream);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }


    public static void generateMysqlByCode(String tableName,
                                           String domainName,
                                           String directory,
                                           String basePackageName,
                                           String shortPackageName,
                                           String projectName,
                                           String jdbcConnection,
                                           String username,
                                           String password) throws IOException, InvalidConfigurationException, SQLException, InterruptedException {
        //create config
        Configuration config = new Configuration();
        //create context
        Context context = new Context(ModelType.CONDITIONAL);
        context.setId("MYSQL");
        context.setTargetRuntime("MyBatis3");

        //create CommentGeneratorConfiguration
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        // context add commonGeneratorConfiguration
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        //create jdbc Connection
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(jdbcConnection);
        jdbcConnectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
        jdbcConnectionConfiguration.setUserId(username);
        jdbcConnectionConfiguration.setPassword(password);
        //context set JDBCConnectionConfiguration
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        //create JavaTypeResolverConfiguration
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
        //context add JavaTypeResolverConfiguration
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        //create JavaModelGeneratorConfiguration
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(basePackageName + ".entity." + shortPackageName);
        javaModelGeneratorConfiguration.setTargetProject(mkDir(directory + "/" + projectName + "/" + projectName + "-entity/src/main/java"));
        javaModelGeneratorConfiguration.addProperty("enableSubPackages", "true");
        javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
        //context add JavaModelGeneratorConfiguration
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        //create SqlMapGeneratorConfiguration
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(basePackageName + ".xml." + shortPackageName);
        sqlMapGeneratorConfiguration.setTargetProject(mkDir(directory + "/" + projectName + "/" + projectName + "-api/src/main/resources"));
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
        //context add SqlMapGeneratorConfiguration
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        //create JavaClientGeneratorConfiguration
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage(basePackageName + ".dao." + shortPackageName);
        javaClientGeneratorConfiguration.setTargetProject(mkDir(directory + "/" + projectName + "/" + projectName + "-dao/src/main/java"));
        javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        //context add javaClientGeneratorConfiguration
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        //create TableConfiguration
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        tableConfiguration.setDomainObjectName(domainName);
        //context add TableConfiguration
        context.addTableConfiguration(tableConfiguration);

        //config add context
        config.addContext(context);

        List<String> warnings = new ArrayList<String>();
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);

    }

    public static String mkDir(String dir) {
        File target = new File(dir);
        if (!target.exists()) {
            boolean mkdir = target.mkdirs();
            logger.info(mkdir + "");
        }
        return target.getAbsolutePath();
    }
}
