/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 * Copyright 2018 SmartBear Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen.languages;

import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.CodegenType;
import org.openapitools.codegen.SupportingFile;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import io.swagger.v3.oas.models.media.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RubyClientCodegen extends AbstractRubyCodegen {
    private static final Logger LOGGER = LoggerFactory.getLogger(RubyClientCodegen.class);
    public static final String GEM_NAME = "gemName";
    public static final String MODULE_NAME = "moduleName";
    public static final String GEM_VERSION = "gemVersion";
    public static final String GEM_LICENSE = "gemLicense";
    public static final String GEM_REQUIRED_RUBY_VERSION = "gemRequiredRubyVersion";
    public static final String GEM_HOMEPAGE = "gemHomepage";
    public static final String GEM_SUMMARY = "gemSummary";
    public static final String GEM_DESCRIPTION = "gemDescription";
    public static final String GEM_AUTHOR = "gemAuthor";
    public static final String GEM_AUTHOR_EMAIL = "gemAuthorEmail";

    protected String gemName;
    protected String moduleName;
    protected String gemVersion = "1.0.0";
    protected String specFolder = "spec";
    protected String libFolder = "lib";
    protected String gemLicense = "proprietary";
    protected String gemRequiredRubyVersion = ">= 1.9";
    protected String gemHomepage = "http://org.openapitools";
    protected String gemSummary = "A ruby wrapper for the REST APIs";
    protected String gemDescription = "This gem maps to a REST API";
    protected String gemAuthor = "";
    protected String gemAuthorEmail = "";
    protected String apiDocPath = "docs/";
    protected String modelDocPath = "docs/";

    protected static int emptyMethodNameCounter = 0;

    public RubyClientCodegen() {
        super();

        // clear import mapping (from default generator) as ruby does not use it
        // at the moment
        importMapping.clear();

        modelPackage = "models";
        apiPackage = "api";
        outputFolder = "generated-code" + File.separator + "ruby";
        modelTemplateFiles.put("model.mustache", ".rb");
        apiTemplateFiles.put("api.mustache", ".rb");
        modelDocTemplateFiles.put("model_doc.mustache", ".md");
        apiDocTemplateFiles.put("api_doc.mustache", ".md");
        embeddedTemplateDir = templateDir = "ruby-client";

        modelTestTemplateFiles.put("model_test.mustache", ".rb");
        apiTestTemplateFiles.put("api_test.mustache", ".rb");

        // default HIDE_GENERATION_TIMESTAMP to true
        hideGenerationTimestamp = Boolean.TRUE;

        // local variable names used in API methods (endpoints)
        for (String word : Arrays.asList(
                "local_var_path", "query_params", "header_params", "_header_accept", "_header_accept_result",
                "_header_content_type", "form_params", "post_body", "auth_names")) {
            reservedWords.add(word.toLowerCase());
        }


        // primitives in ruby lang
        languageSpecificPrimitives.add("int");
        languageSpecificPrimitives.add("array");
        languageSpecificPrimitives.add("map");
        languageSpecificPrimitives.add("string");
        // primitives in the typeMapping
        languageSpecificPrimitives.add("BOOLEAN");
        typeMapping.put("boolean", "BOOLEAN");

        // remove modelPackage and apiPackage added by default
        Iterator<CliOption> itr = cliOptions.iterator();
        while (itr.hasNext()) {
            CliOption opt = itr.next();
            if (CodegenConstants.MODEL_PACKAGE.equals(opt.getOpt()) ||
                    CodegenConstants.API_PACKAGE.equals(opt.getOpt())) {
                itr.remove();
            }
        }
        cliOptions.add(new CliOption(GEM_NAME, "gem name (convention: underscore_case).").
                defaultValue("openapi_client"));
        cliOptions.add(new CliOption(MODULE_NAME, "top module name (convention: CamelCase, usually corresponding" +
                " to gem name).").defaultValue("OpenAPIClient"));
        cliOptions.add(new CliOption(GEM_VERSION, "gem version.").defaultValue("1.0.0"));

        cliOptions.add(new CliOption(GEM_LICENSE, "gem license. ").
                defaultValue("proprietary"));

        cliOptions.add(new CliOption(GEM_REQUIRED_RUBY_VERSION, "gem required Ruby version. ").
                defaultValue(">= 1.9"));

        cliOptions.add(new CliOption(GEM_HOMEPAGE, "gem homepage. ").
                defaultValue("http://org.openapitools"));

        cliOptions.add(new CliOption(GEM_SUMMARY, "gem summary. ").
                defaultValue("A ruby wrapper for the REST APIs"));

        cliOptions.add(new CliOption(GEM_DESCRIPTION, "gem description. ").
                defaultValue("This gem maps to a REST API"));

        cliOptions.add(new CliOption(GEM_AUTHOR, "gem author (only one is supported)."));

        cliOptions.add(new CliOption(GEM_AUTHOR_EMAIL, "gem author email (only one is supported)."));

        cliOptions.add(new CliOption(CodegenConstants.HIDE_GENERATION_TIMESTAMP, CodegenConstants.HIDE_GENERATION_TIMESTAMP_DESC).
                defaultValue(Boolean.TRUE.toString()));

    }

    @Override
    public void processOpts() {
        super.processOpts();

        if (additionalProperties.containsKey(GEM_NAME)) {
            setGemName((String) additionalProperties.get(GEM_NAME));
        }
        if (additionalProperties.containsKey(MODULE_NAME)) {
            setModuleName((String) additionalProperties.get(MODULE_NAME));
        }

        if (gemName == null && moduleName == null) {
            setGemName("openapi_client");
            setModuleName(generateModuleName(gemName));
        } else if (gemName == null) {
            setGemName(generateGemName(moduleName));
        } else if (moduleName == null) {
            setModuleName(generateModuleName(gemName));
        }

        additionalProperties.put(GEM_NAME, gemName);
        additionalProperties.put(MODULE_NAME, moduleName);

        if (additionalProperties.containsKey(GEM_VERSION)) {
            setGemVersion((String) additionalProperties.get(GEM_VERSION));
        } else {
            // not set, pass the default value to template
            additionalProperties.put(GEM_VERSION, gemVersion);
        }

        if (additionalProperties.containsKey(GEM_LICENSE)) {
            setGemLicense((String) additionalProperties.get(GEM_LICENSE));
        }

        if (additionalProperties.containsKey(GEM_REQUIRED_RUBY_VERSION)) {
            setGemRequiredRubyVersion((String) additionalProperties.get(GEM_REQUIRED_RUBY_VERSION));
        }

        if (additionalProperties.containsKey(GEM_HOMEPAGE)) {
            setGemHomepage((String) additionalProperties.get(GEM_HOMEPAGE));
        }

        if (additionalProperties.containsKey(GEM_SUMMARY)) {
            setGemSummary((String) additionalProperties.get(GEM_SUMMARY));
        }

        if (additionalProperties.containsKey(GEM_DESCRIPTION)) {
            setGemDescription((String) additionalProperties.get(GEM_DESCRIPTION));
        }

        if (additionalProperties.containsKey(GEM_AUTHOR)) {
            setGemAuthor((String) additionalProperties.get(GEM_AUTHOR));
        }

        if (additionalProperties.containsKey(GEM_AUTHOR_EMAIL)) {
            setGemAuthorEmail((String) additionalProperties.get(GEM_AUTHOR_EMAIL));
        }

        // make api and model doc path available in mustache template
        additionalProperties.put("apiDocPath", apiDocPath);
        additionalProperties.put("modelDocPath", modelDocPath);

        // use constant model/api package (folder path)
        setModelPackage("models");
        setApiPackage("api");

        supportingFiles.add(new SupportingFile("gemspec.mustache", "", gemName + ".gemspec"));
        supportingFiles.add(new SupportingFile("gem.mustache", libFolder, gemName + ".rb"));
        String gemFolder = libFolder + File.separator + gemName;
        supportingFiles.add(new SupportingFile("api_client.mustache", gemFolder, "api_client.rb"));
        supportingFiles.add(new SupportingFile("api_error.mustache", gemFolder, "api_error.rb"));
        supportingFiles.add(new SupportingFile("configuration.mustache", gemFolder, "configuration.rb"));
        supportingFiles.add(new SupportingFile("version.mustache", gemFolder, "version.rb"));
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
        supportingFiles.add(new SupportingFile("git_push.sh.mustache", "", "git_push.sh"));
        supportingFiles.add(new SupportingFile("gitignore.mustache", "", ".gitignore"));
        supportingFiles.add(new SupportingFile("Rakefile.mustache", "", "Rakefile"));
        supportingFiles.add(new SupportingFile("Gemfile.mustache", "", "Gemfile"));
        supportingFiles.add(new SupportingFile("rubocop.mustache", "", ".rubocop.yml"));

        // test files should not be overwritten
        writeOptional(outputFolder, new SupportingFile("rspec.mustache", "", ".rspec"));
        writeOptional(outputFolder, new SupportingFile("spec_helper.mustache", specFolder, "spec_helper.rb"));
        writeOptional(outputFolder, new SupportingFile("configuration_spec.mustache", specFolder, "configuration_spec.rb"));
        writeOptional(outputFolder, new SupportingFile("api_client_spec.mustache", specFolder, "api_client_spec.rb"));
        // not including base object test as the moment as not all API has model
        //writeOptional(outputFolder, new SupportingFile("base_object_spec.mustache", specFolder, "base_object_spec.rb"));
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    @Override
    public String getName() {
        return "ruby";
    }

    @Override
    public String getHelp() {
        return "Generates a Ruby client library.";
    }

    /**
     * Generate Ruby module name from the gem name, e.g. use "OpenAPIClient" for "openapi_client".
     *
     * @param gemName Ruby gem name
     * @return Ruby module naame
     */
    @SuppressWarnings("static-method")
    public String generateModuleName(String gemName) {
        return camelize(gemName.replaceAll("[^\\w]+", "_"));
    }

    /**
     * Generate Ruby gem name from the module name, e.g. use "openapi_client" for "OpenAPIClient".
     *
     * @param moduleName Ruby module naame
     * @return Ruby gem name
     */
    @SuppressWarnings("static-method")
    public String generateGemName(String moduleName) {
        return underscore(moduleName.replaceAll("[^\\w]+", ""));
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + File.separator + libFolder + File.separator + gemName + File.separator + apiPackage.replace("/", File.separator);
    }

    @Override
    public String modelFileFolder() {
        return outputFolder + File.separator + libFolder + File.separator + gemName + File.separator + modelPackage.replace("/", File.separator);
    }

    @Override
    public String apiTestFileFolder() {
        return outputFolder + File.separator + specFolder + File.separator + apiPackage.replace("/", File.separator);
    }

    @Override
    public String modelTestFileFolder() {
        return outputFolder + File.separator + specFolder + File.separator + modelPackage.replace("/", File.separator);
    }

    @Override
    public String apiDocFileFolder() {
        return (outputFolder + "/" + apiDocPath).replace('/', File.separatorChar);
    }

    @Override
    public String modelDocFileFolder() {
        return (outputFolder + "/" + modelDocPath).replace('/', File.separatorChar);
    }

    @Override
    public String getSchemaType(Schema schema) {
        String openAPIType = super.getSchemaType(schema);
        String type = null;
        if (typeMapping.containsKey(openAPIType)) {
            type = typeMapping.get(openAPIType);
            if (languageSpecificPrimitives.contains(type)) {
                return type;
            }
        } else {
            type = openAPIType;
        }

        if (type == null) {
            return null;
        }

        return toModelName(type);
    }

    @Override
    public String toModelName(String name) {
        name = sanitizeName(name); // FIXME: a parameter should not be assigned. Also declare the methods parameters as 'final'.

        if (!StringUtils.isEmpty(modelNamePrefix)) {
            name = modelNamePrefix + "_" + name;
        }

        if (!StringUtils.isEmpty(modelNameSuffix)) {
            name = name + "_" + modelNameSuffix;
        }

        // model name cannot use reserved keyword, e.g. return
        if (isReservedWord(name)) {
            String modelName = camelize("Model" + name);
            LOGGER.warn(name + " (reserved word) cannot be used as model name. Renamed to " + modelName);
            return modelName;
        }

        // model name starts with number
        if (name.matches("^\\d.*")) {
            LOGGER.warn(name + " (model name starts with number) cannot be used as model name. Renamed to " + camelize("model_" + name));
            name = "model_" + name; // e.g. 200Response => Model200Response (after camelize)
        }

        // camelize the model name
        // phone_number => PhoneNumber
        return camelize(name);
    }

    @Override
    public String toModelFilename(String name) {
        return underscore(toModelName(name));
    }

    @Override
    public String toModelDocFilename(String name) {
        return toModelName(name);
    }

    @Override
    public String toApiFilename(String name) {
        // replace - with _ e.g. created-at => created_at
        name = name.replaceAll("-", "_"); // FIXME: a parameter should not be assigned. Also declare the methods parameters as 'final'.

        // e.g. PhoneNumberApi.rb => phone_number_api.rb
        return underscore(name) + "_api";
    }

    @Override
    public String toApiDocFilename(String name) {
        return toApiName(name);
    }

    @Override
    public String toApiTestFilename(String name) {
        return toApiFilename(name) + "_spec";
    }

    @Override
    public String toModelTestFilename(String name) {
        return toModelFilename(name) + "_spec";
    }

    @Override
    public String toApiName(String name) {
        if (name.length() == 0) {
            return "DefaultApi";
        }
        // e.g. phone_number_api => PhoneNumberApi
        return camelize(name) + "Api";
    }

    @Override
    public String toEnumValue(String value, String datatype) {
        if ("Integer".equals(datatype) || "Float".equals(datatype)) {
            return value;
        } else {
            return "'" + escapeText(value) + "'";
        }
    }

    @Override
    public String toEnumVarName(String name, String datatype) {
        if (name.length() == 0) {
            return "EMPTY";
        }

        // number
        if ("Integer".equals(datatype) || "Float".equals(datatype)) {
            String varName = name;
            varName = varName.replaceAll("-", "MINUS_");
            varName = varName.replaceAll("\\+", "PLUS_");
            varName = varName.replaceAll("\\.", "_DOT_");
            return varName;
        }

        // string
        String enumName = sanitizeName(underscore(name).toUpperCase());
        enumName = enumName.replaceFirst("^_", "");
        enumName = enumName.replaceFirst("_$", "");

        if (enumName.matches("\\d.*")) { // starts with number
            return "N" + enumName;
        } else {
            return enumName;
        }
    }

    @Override
    public String toEnumName(CodegenProperty property) {
        String enumName = underscore(toModelName(property.name)).toUpperCase();
        enumName = enumName.replaceFirst("^_", "");
        enumName = enumName.replaceFirst("_$", "");

        if (enumName.matches("\\d.*")) { // starts with number
            return "N" + enumName;
        } else {
            return enumName;
        }
    }

    @Override
    public Map<String, Object> postProcessModels(Map<String, Object> objs) {
        // process enum in models
        return postProcessModelsEnum(objs);
    }

    @Override
    public String toOperationId(String operationId) {
        // rename to empty_method_name_1 (e.g.) if method name is empty
        if (StringUtils.isEmpty(operationId)) {
            operationId = underscore("empty_method_name_" + emptyMethodNameCounter++);
            LOGGER.warn("Empty method name (operationId) found. Renamed to " + operationId);
            return operationId;
        }

        // method name cannot use reserved keyword, e.g. return
        if (isReservedWord(operationId)) {
            String newOperationId = underscore("call_" + operationId);
            LOGGER.warn(operationId + " (reserved word) cannot be used as method name. Renamed to " + newOperationId);
            return newOperationId;
        }

        return underscore(sanitizeName(operationId));
    }

    @Override
    public String toApiImport(String name) {
        return gemName + "/" + apiPackage() + "/" + toApiFilename(name);
    }

    @Override
    public void setParameterExampleValue(CodegenParameter p) {
        String example;

        if (p.defaultValue == null) {
            example = p.example;
        } else {
            p.example = p.defaultValue;
            return;
        }

        String type = p.baseType;
        if (type == null) {
            type = p.dataType;
        }

        if ("String".equals(type)) {
            if (example == null) {
                example = p.paramName + "_example";
            }
            example = "'" + escapeText(example) + "'";
        } else if ("Integer".equals(type)) {
            if (example == null) {
                example = "56";
            }
        } else if ("Float".equals(type)) {
            if (example == null) {
                example = "3.4";
            }
        } else if ("BOOLEAN".equals(type)) {
            if (example == null) {
                example = "true";
            }
        } else if ("File".equals(type)) {
            if (example == null) {
                example = "/path/to/file";
            }
            example = "File.new('" + escapeText(example) + "')";
        } else if ("Date".equals(type)) {
            if (example == null) {
                example = "2013-10-20";
            }
            example = "Date.parse('" + escapeText(example) + "')";
        } else if ("DateTime".equals(type)) {
            if (example == null) {
                example = "2013-10-20T19:20:30+01:00";
            }
            example = "DateTime.parse('" + escapeText(example) + "')";
        } else if (!languageSpecificPrimitives.contains(type)) {
            // type is a model class, e.g. User
            example = moduleName + "::" + type + ".new";
        }

        if (example == null) {
            example = "nil";
        } else if (Boolean.TRUE.equals(p.isListContainer)) {
            example = "[" + example + "]";
        } else if (Boolean.TRUE.equals(p.isMapContainer)) {
            example = "{'key' => " + example + "}";
        }

        p.example = example;
    }

    public void setGemName(String gemName) {
        this.gemName = gemName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setGemVersion(String gemVersion) {
        this.gemVersion = gemVersion;
    }

    public void setGemDescription(String gemDescription) {
        this.gemDescription = gemDescription;
    }

    public void setGemSummary(String gemSummary) {
        this.gemSummary = gemSummary;
    }

    public void setGemLicense(String gemLicense) {
        this.gemLicense = gemLicense;
    }

    public void setGemRequiredRubyVersion(String gemRequiredRubyVersion) {
        this.gemRequiredRubyVersion = gemRequiredRubyVersion;
    }

    public void setGemHomepage(String gemHomepage) {
        this.gemHomepage = gemHomepage;
    }

    public void setGemAuthor(String gemAuthor) {
        this.gemAuthor = gemAuthor;
    }

    public void setGemAuthorEmail(String gemAuthorEmail) {
        this.gemAuthorEmail = gemAuthorEmail;
    }

    @Override
    public boolean shouldOverwrite(String filename) {
        // skip spec file as the file might have been updated with new test cases
        return !(skipOverwrite && new File(filename).exists());
        //
        //return super.shouldOverwrite(filename) && !filename.endsWith("_spec.rb");
    }
}
