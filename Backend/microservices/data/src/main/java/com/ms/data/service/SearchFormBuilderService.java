package com.ms.data.service;

import com.ms.data.dto.form.ApiCombination;
import com.ms.data.dto.form.CombinationFieldReference;
import com.ms.data.dto.form.HtmlForm;
import com.ms.data.dto.form.input.HtmlInput;
import com.ms.data.dto.xml.Combination;
import com.ms.data.dto.xml.Field;
import com.ms.data.dto.xml.InterfaceSchema;
import com.ms.data.service.builders.HtmlInputBuilder;
import com.ms.data.service.builders.HtmlInputBuilderByNameAndType;
import com.ms.data.service.builders.HtmlInputBuilderByType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchFormBuilderService {

    private final List<HtmlInputBuilder> builders = new ArrayList<>();

    @Autowired
    public SearchFormBuilderService(
            List<HtmlInputBuilderByNameAndType> buildersByNameAndType,
            List<HtmlInputBuilderByType> buildersByType
    ) {
        builders.addAll(buildersByNameAndType);
        builders.addAll(buildersByType);
    }

    public HtmlInput buildInput(Field field) {
        HtmlInputBuilder builder = builders.stream().filter(b -> b.canBuild(field)).findFirst().orElseThrow();
        return builder.build(field, this);
    }

    public HtmlForm buildForm(InterfaceSchema schema) {
        HtmlForm htmlForm = new HtmlForm();
        htmlForm.setName(schema.getTransaction().getName());

        for (Field field : schema.getTransaction().getFields()) {
            HtmlInput htmlInput = buildInput(field);
            htmlForm.getInputs().add(htmlInput);
        }

        for (Combination combination : schema.getTransaction().getCombinations()) {
            ApiCombination apiCombination = new ApiCombination(combination.getPrimaryFieldReference());
            for (Field mandatoryField : combination.getRequirements().getFields().getMandatoryFields()) {
                apiCombination.getFields().add(
                        new CombinationFieldReference(mandatoryField.getReference(), true)
                );
            }

            for (Field optionalField : combination.getRequirements().getFields().getOptionalFields()) {
                apiCombination.getFields().add(
                        new CombinationFieldReference(optionalField.getReference(), false)
                );
            }

            htmlForm.getCombinations().add(apiCombination);
        }

        return htmlForm;
    }
}
