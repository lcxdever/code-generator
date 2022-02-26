package com.roc.generator.unittest.model;

import com.intellij.testFramework.fixtures.*;
import com.roc.generator.model.ClassInfo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于测试各种代码
 *
 * @author yuman
 * @date 2022-02-14
 */
public class MainTest {

    @Test
    public void test() {

//        ClassInfo classInfo = ClassInfo.fromClassNameText("java.lang.List<java.lang.String<?>, java.Lang.String<java.lang.Object>>");
//        System.out.println(classInfo.getClassList());
//        System.out.println(MdUtil.commentFormat("/**\n" +
//                "     * 根据名字查询\n" +
//                "     *\n" +
//                "     * @param name 名字\n" +
//                "     * @return 人\n" +
//                "     */\n" +
//                "    List<Person> selectByName(String name);"));
        String str = "A<B<C,F>,D>";
        List<String> list = new ArrayList<>();
        int flag = 0;
        int areaFlag = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '<') {
                flag = i + 1;
            }
        }
    }

    @Test
    public void testPlugin() throws Exception {
        TestFixtureBuilder<IdeaProjectTestFixture> projectBuilder =
                IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder("code-generator");

        // Repeat the following line for each module
//        JavaModuleFixtureBuilder moduleFixtureBuilder =
//                projectBuilder.addModule(JavaModuleFixtureBuilderImpl.class);

        JavaCodeInsightTestFixture myFixture = JavaTestFixtureFactory.getFixtureFactory()
                .createCodeInsightFixture(projectBuilder.getFixture());
        myFixture.setUp();
        System.out.println(myFixture.getProject());
    }
}