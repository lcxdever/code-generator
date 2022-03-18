package com.roc.generator.unittest.model;

import com.intellij.testFramework.fixtures.*;
import com.roc.generator.model.TypeInfo;
import com.roc.generator.util.GsonUtil;
import org.junit.Test;

import java.util.Date;

/**
 * 用于测试各种代码
 *
 * @author yuman @date 2022-02-14
 */
public class MainTest {

    @Test
    public void test() {
//        TypeInfo typeInfo = TypeInfo.fromNameGenericsCanonical("java.List<c.Create<a.Test, c.Just>>");
//        System.out.println(typeInfo.getNameGenericsSimple());
        Tmp tmp = new Tmp();
        tmp.setDate(new Date());
        System.out.println(GsonUtil.prettyJson(tmp));
    }

    public static class Tmp {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
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