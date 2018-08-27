package cn.tangzy.cg;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.codehaus.plexus.util.FileUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangzhiyuan@bitnei.cn
 * @date 8/27/18
 **/
public class MybatisCodeGeneratorHelper {
    private static Logger logger = LoggerFactory.getLogger(GeneratorMain.class);
    private freemarker.template.Configuration freemarkerCfg;
    private Map<String, String> properties = new HashMap<>();
    private String directory;
    private String basePackageName;
    private String projectName;
    private String jdbcConnection;
    private String username;
    private String password;

    public MybatisCodeGeneratorHelper(String directory, String basePackageName, String projectName, String jdbcConnection, String username, String password) {
        this.directory = directory;
        this.basePackageName = basePackageName;
        this.projectName = projectName;
        this.jdbcConnection = jdbcConnection;
        this.username = username;
        this.password = password;
        initConfiguration();
        properties.put("basePackageName", basePackageName);
        File file = new File(directory + File.separator + projectName);
        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initConfiguration() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_22);
//            cfg.setDirectoryForTemplateLoading(new File("/ftl"));
        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "ftl");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.freemarkerCfg = cfg;

    }

    public void generate(String tableName,
                         String domainName,
                         String shortPackageName) {
        properties.put("shortPackageName", shortPackageName);
        properties.put("domainName", domainName);
        properties.put("domainNameVars", firstToLower(domainName));
        try {
            generateEntity(tableName, domainName, shortPackageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        generateMapper();
        generateService();

    }

    public void generateEntity(String tableName,
                               String domainName,
                               String shortPackageName) throws IOException, InvalidConfigurationException, SQLException, InterruptedException {
        //create config
        Configuration config = new Configuration();
        //create context
        Context context = new Context(ModelType.CONDITIONAL);
        context.setId("MYSQL");
        context.setTargetRuntime("MyBatis3");
        context.addProperty("javaFileEncoding", "utf-8");
        context.addProperty("autoDelimitKeywords", "true");
        context.addProperty("beginningDelimiter", "`");
        context.addProperty("endingDelimiter", "`");

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

//        create JavaClientGeneratorConfiguration
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage(basePackageName + ".dao." + shortPackageName);
        javaClientGeneratorConfiguration.setTargetProject(mkDir(directory + "/" + projectName + "/" + projectName + "-dao/src/main/java"));
        javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
//        context add javaClientGeneratorConfiguration
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


    private void generateMapper() {
        String mapper = directory + File.separator +
                projectName + File.separator +
                projectName + "-dao/src/main/java" + File.separator +
                basePackageName.replace(".", File.separator) +
                File.separator + "dao" + File.separator + properties.get("shortPackageName") +
                File.separator + properties.get("domainName") + "Mapper.java";

        generateFile(createFile(mapper), "Mapper.ftl");
    }

    private void generateService() {

        String service = directory + File.separator +
                projectName + File.separator +
                projectName + "-service/src/main/java" + File.separator +
                basePackageName.replace(".", File.separator) +
                File.separator + "service" + File.separator + properties.get("shortPackageName") +
                File.separator + properties.get("domainName") + "Service.java";

        generateFile(createFile(service), "Service.ftl");

        String serviceImpl = directory + File.separator +
                projectName + File.separator +
                projectName + "-service/src/main/java" + File.separator +
                basePackageName.replace(".", File.separator) +
                File.separator + "service" + File.separator + properties.get("shortPackageName") +
                File.separator + "impl" + File.separator + properties.get("domainName") + "ServiceImpl.java";

        generateFile(createFile(serviceImpl), "ServiceImpl.ftl");

    }

    private String firstToLower(String str) {
        char c = str.charAt(0);
        return (c + "").toLowerCase() + str.substring(1);
    }

    private String mkDir(String dir) {
        File target = new File(dir);
        if (!target.exists()) {
            boolean mkdir = target.mkdirs();
            logger.info(mkdir + "");
        }
        return target.getAbsolutePath();
    }

    private String createFile(String filePath) {
        File file = new File(filePath);
        String parent = file.getParent();
        mkDir(parent);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    private void generateFile(String mapper, String fltFile) {
        try {
            Template temp = freemarkerCfg.getTemplate(fltFile);
            Writer out = new OutputStreamWriter(new FileOutputStream(mapper));
            temp.process(properties, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
}
