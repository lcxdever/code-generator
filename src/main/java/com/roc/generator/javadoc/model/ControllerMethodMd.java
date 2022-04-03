package com.roc.generator.javadoc.model;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.roc.generator.model.AnnotationInfo;
import com.roc.generator.model.MethodInfo;
import com.roc.generator.util.GsonUtil;
import com.roc.generator.util.JavaJsonUtil;
import com.roc.generator.util.MdUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.Optional;

/**
 * Controller 方法 markdown 描述
 *
 * @author 鱼蛮 on 2022/2/20
 **/
@Getter
@Setter
@ToString
public class ControllerMethodMd extends MethodMd {

    public static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    public static final String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    public static final String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
    public static final String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    public static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";
    public static final String CONTROLLER = "org.springframework.stereotype.Controller";
    public static final String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";

    /**
     * 请求地址
     */
    private String requestUri;

    /**
     * 请求的方式
     */
    private String requestMethod;

    /**
     * 请求类型
     */
    private String requestType;

    public ControllerMethodMd(MethodInfo methodInfo) {
        super(methodInfo);

        PsiMethod psiMethod = methodInfo.getPsiMethod();
        PsiClass ctcClass = Objects.requireNonNull(PsiUtil.getTopLevelClass(psiMethod));
        // 获取类上 RequestMapping 的注解
        PsiAnnotation ctlRequestAnno = ctcClass.getAnnotation(REQUEST_MAPPING);
        String uriPrefix = Optional.ofNullable(ctlRequestAnno)
                .map(AnnotationInfo::fromPsiAnnotation).map(e -> e.getAttributeValue("value"))
                .orElse("");
        String uriSuffix = "";
        for (PsiAnnotation ann : psiMethod.getAnnotations()) {
            AnnotationInfo annInfo = AnnotationInfo.fromPsiAnnotation(ann);
            switch (annInfo.getTypeInfo().getNameCanonical()) {
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
        requestUri = uriPrefix + uriSuffix;
        // 获取请求类型
        requestType = MdUtil.getRequestType(methodInfo);
        // 如果参数中有 RequestBody 注解，自动生成示例
        for (PsiParameter psiParameter : methodInfo.getParameters()) {
            if (psiParameter.hasAnnotation(REQUEST_BODY)) {
                this.setParameterEg(GsonUtil.prettyJson(JavaJsonUtil.genJsonFromPsiType(psiParameter.getType())));
            }
        }
    }

    @Override
    protected boolean isValidParameter(PsiParameter psiParameter) {
        return MdUtil.isValidControllerParma(psiParameter);
    }
}
