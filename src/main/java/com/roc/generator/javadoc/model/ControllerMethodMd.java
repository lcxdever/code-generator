package com.roc.generator.javadoc.model;

import com.intellij.psi.*;
import com.roc.generator.model.AnnotationInfo;
import com.roc.generator.model.MethodInfo;
import com.roc.generator.util.MdUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author 鱼蛮 on 2022/2/20
 **/
@Getter
@Setter
@ToString
public class ControllerMethodMd extends MethodMd{

    public static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    public static final String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    public static final String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
    public static final String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    public static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";

    /***
     * 请求地址
     */
    private String requestMapping;

    /**
     * 请求的方法
     */
    private String requestMethod;

    /**
     * 请求类型
     */
    private String requestType;

    public ControllerMethodMd() {}

    public ControllerMethodMd(MethodInfo methodInfo) {
        super(methodInfo, true);

        PsiMethod psiMethod = methodInfo.getPsiMethod();
        PsiElement psiElement = psiMethod.getParent();
        if (Objects.isNull(psiElement) || !(psiElement instanceof PsiClass)) {
            return;
        }
        PsiClass ctcClass = (PsiClass)psiElement;
        // 获取类上的注解
        PsiAnnotation ctlRequestAnno = ctcClass.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        String uriPrefix = "";
        if (Objects.nonNull(ctlRequestAnno)) {
            AnnotationInfo cltAnnoInfo = AnnotationInfo.fromPsiAnnotation(ctlRequestAnno);
            uriPrefix = cltAnnoInfo.getAttributeValue("value");
        }
        String uriSuffix = "";
        for (PsiAnnotation ann : psiMethod.getAnnotations()) {
            AnnotationInfo annInfo = AnnotationInfo.fromPsiAnnotation(ann);
            String annName = annInfo.getClassInfo().getClassNameFull();
            switch (annName) {
                case REQUEST_MAPPING:
                    String method = annInfo.getAttributeValue("method");
                    if (Objects.equals(method, "RequestMethod.POST")) {
                        requestMethod = "POST";
                    } else if (Objects.equals(method, "RequestMethod.GET")) {
                        requestMethod = "GET";
                    } else {
                        requestMethod = "GET/POST";
                    }
                    uriSuffix = annInfo.getAttributeValue("value");
                    break;
                case POST_MAPPING:
                    requestMethod = "POST";
                    uriSuffix = annInfo.getAttributeValue("value");
                    break;
                case GET_MAPPING:
                    requestMethod = "GET";
                    uriSuffix = annInfo.getAttributeValue("value");
                    break;
                default:
            }
        }
        requestMapping = uriPrefix + uriSuffix;
        // 获取请求类型
        if (MdUtil.isRequestBody(methodInfo)) {
            requestType = "BODY";
        }
    }
}
