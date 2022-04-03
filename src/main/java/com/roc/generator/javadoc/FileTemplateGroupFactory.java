package com.roc.generator.javadoc;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.roc.generator.PluginIcons;

/**
 * 模板组工厂
 *
 * @author 鱼蛮 Date 2022/4/3
 */
public class FileTemplateGroupFactory implements FileTemplateGroupDescriptorFactory {

    /** 接口方法模板 */
    public static final String MD_INTERFACE_METHOD_DOC = "doc-interface-method.md";
    /** controller方法模板 */
    public static final String MD_CONTROLLER_METHOD_DOC = "doc-controller-method.md";
    /** 接口方法模板 */
    public static final String MD_DOMAIN_DOC = "doc-domain.md";

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        // 定义模板组
        FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("Roc Code", PluginIcons.FILE_TEMPLATE_ICON);

        group.addTemplate(new FileTemplateDescriptor(MD_INTERFACE_METHOD_DOC));
        group.addTemplate(new FileTemplateDescriptor(MD_CONTROLLER_METHOD_DOC));
        group.addTemplate(new FileTemplateDescriptor(MD_DOMAIN_DOC));

        return group;
    }
}
