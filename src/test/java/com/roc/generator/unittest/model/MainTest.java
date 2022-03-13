package com.roc.generator.unittest.model;

import com.intellij.testFramework.fixtures.*;
import com.roc.generator.model.TypeInfo;
import org.junit.Test;

/**
 * 用于测试各种代码
 *
 * @author yuman
 * @date 2022-02-14
 */
public class MainTest {

    @Test
    public void test() {
        TypeInfo typeInfo = TypeInfo.fromNameGenericsCanonical("java.List<c.Create<a.Test, c.Just>>");
        System.out.println(typeInfo.getNameGenericsSimple());
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