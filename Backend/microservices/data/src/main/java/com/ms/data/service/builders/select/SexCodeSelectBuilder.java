package com.ms.data.service.builders.select;

import com.customstarter.model.schema.input.HtmlInput;
import com.customstarter.model.schema.input.Select;
import com.customstarter.model.schema.input.SelectOption;
import com.ms.data.dto.xml.Field;
import com.ms.data.service.SearchFormBuilderService;
import com.ms.data.service.builders.HtmlInputBuilderByNameAndType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SexCodeSelectBuilder extends HtmlInputBuilderByNameAndType {

    private static final List<SelectOption> OPTIONS = List.of(
            new SelectOption("", "Unknown"),
            new SelectOption("M", "Male"),
            new SelectOption("F", "Female")
    );

    @Override
    protected boolean canBuild(String name, String type) {
        return "SexCode".equals(name);
    }

    @Override
    public HtmlInput build(Field field, SearchFormBuilderService searchFormBuilderService) {
        Select select = new Select(field.getName(), field.getDescription());
        select.getOptions().add(SelectOption.UNKNOWN);
        select.getOptions().addAll(OPTIONS);

        return select;
    }
}
