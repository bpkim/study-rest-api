package com.bpkim.studyrestapi.common;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.function.Function;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class PagedModelUtil {
    public static <T> PagedModel<EntityModel<T>> getEntityModels(PagedResourcesAssembler<T> assembler,
                                                                 Page<T> page,
                                                                 Class<?> clazz,
                                                                 Function<T, ?> selfLinkFunc ){
        WebMvcLinkBuilder webMvcLinkBuilder = linkTo(clazz);
        return assembler.toModel(page, model -> LinkResource.of(webMvcLinkBuilder, model, selfLinkFunc::apply));
    }

    public static <T> PagedModel<EntityModel<T>> getEntityModels(PagedResourcesAssembler<T> assembler,
                                                                 Page<T> page,
                                                                 WebMvcLinkBuilder builder,
                                                                 Function<T, ?> selfLinkFunc ){
        return assembler.toModel(page, model -> LinkResource.of(builder, model, selfLinkFunc::apply));
    }
}