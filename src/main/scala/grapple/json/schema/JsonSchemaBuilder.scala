/*
 * Copyright 2026 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grapple.json
package schema

import java.io.{ File, InputStream, OutputStream, Reader, Writer }
import java.nio.file.Path

/** Defines JSON schema builder. */
class JsonSchemaBuilder:
  private var $schema: Option[DialectParam] = None
  private var $id: Option[URIParam] = None
  private var $dynamicRef: Option[URIParam] = None
  private var $dynamicAnchor: Option[String] = None
  private var $vocabulary: Map[String, Boolean] = Map.empty
  private var $ref: Option[URIParam] = None
  private var $anchor: Option[String] = None
  private var $comments: Option[String] = None
  private var $defs: Map[String, JsonSchemaParam] = Map.empty
  private var title: Option[String] = None
  private var description: Option[String] = None
  private var kind: Seq[DataTypeParam] = Nil
  private var defaultValue: Option[JsonValueParam] = None
  private var constValue: Option[JsonValueParam] = None
  private var enumValues: Option[Seq[JsonValueParam]] = None
  private var minLength: Option[Int] = None
  private var maxLength: Option[Int] = None
  private var format: Option[String] = None
  private var pattern: Option[String] = None
  private var contentMediaType: Option[String] = None
  private var contentMediaEncoding: Option[EncodingParam] = None
  private var contentSchema: Option[JsonSchemaParam] = None
  private var minimum: Option[BigDecimal] = None
  private var exclusiveMinimum: Option[BigDecimal] = None
  private var maximum: Option[BigDecimal] = None
  private var exclusiveMaximum: Option[BigDecimal] = None
  private var multipleOf: Option[BigDecimal] = None
  private var prefixItems: Option[Seq[JsonSchemaParam]] = None
  private var items: Option[JsonSchemaParam] = None
  private var minItems: Option[Int] = None
  private var maxItems: Option[Int] = None
  private var contains: Option[JsonSchemaParam] = None
  private var minContains: Option[Int] = None
  private var maxContains: Option[Int] = None
  private var uniqueItems: Option[Boolean] = None
  private var additionalItems: Option[JsonSchemaParam] = None
  private var unevaluatedItems: Option[JsonSchemaParam] = None
  private var propertyNames: Option[JsonSchemaParam] = None
  private var patternProperties: Map[String, JsonSchemaParam] = Map.empty
  private var properties: Map[String, JsonSchemaParam] = Map.empty
  private var minProperties: Option[Int] = None
  private var maxProperties: Option[Int] = None
  private var required: Option[Seq[String]] = None
  private var dependentRequired: Map[String, Seq[String]] = Map.empty
  private var dependentSchemas: Map[String, Seq[JsonSchemaParam]] = Map.empty
  private var additionalProperties: Option[JsonSchemaParam] = None
  private var unevaluatedProperties: Option[JsonSchemaParam] = None
  private var allOf: Option[Seq[JsonSchemaParam]] = None
  private var anyOf: Option[Seq[JsonSchemaParam]] = None
  private var oneOf: Option[Seq[JsonSchemaParam]] = None
  private var not: Option[JsonSchemaParam] = None
  private var ifApply: Option[JsonSchemaParam] = None
  private var thenApply: Option[JsonSchemaParam] = None
  private var elseApply: Option[JsonSchemaParam] = None
  private var exampleValues: Seq[JsonValueParam] = Nil
  private var readOnly: Option[Boolean] = None
  private var writeOnly: Option[Boolean] = None
  private var deprecated: Option[Boolean] = None

  /**
   * Creates JSON schema builder initialized with supplied schema.
   *
   * @param schema initializing schema
   */
  def this(schema: JsonSchema) =
    this()
    schema.$schema.foreach { setSchema(_) }
    schema.$id.foreach { setId(_) }
    schema.$dynamicRef.foreach { setDynamicRef(_) }
    schema.$dynamicAnchor.foreach { setDynamicAnchor(_) }
    setVocabulary(schema.$vocabulary)
    schema.$ref.foreach { setRef(_) }
    schema.$anchor.foreach { setAnchor(_) }
    schema.$comments.foreach { setComments(_) }
    setDefs(schema.$defs)
    schema.title.foreach { setTitle(_) }
    schema.description.foreach { setDescription(_) }
    setKind(schema.kind)
    schema.defaultValue.foreach { setDefaultValue(_) }
    schema.constValue.foreach { setConstValue(_) }
    schema.enumValues.foreach { setEnumValues(_) }
    schema.minLength.foreach { setMinLength(_) }
    schema.maxLength.foreach { setMaxLength(_) }
    schema.format.foreach { setFormat(_) }
    schema.pattern.foreach { setPattern(_) }
    schema.contentMediaType.foreach { setContentMediaType(_) }
    schema.contentMediaEncoding.foreach { setContentMediaEncoding(_) }
    schema.contentSchema.foreach { setContentSchema(_) }
    schema.minimum.foreach { setMinimum(_) }
    schema.exclusiveMinimum.foreach { setExclusiveMinimum(_) }
    schema.maximum.foreach { setMaximum(_) }
    schema.exclusiveMaximum.foreach { setExclusiveMaximum(_) }
    schema.multipleOf.foreach { setMultipleOf(_) }
    schema.prefixItems.foreach { setPrefixItems(_) }
    schema.items.foreach { setItems(_) }
    schema.minItems.foreach { setMinItems(_) }
    schema.maxItems.foreach { setMaxItems(_) }
    schema.contains.foreach { setContains(_) }
    schema.minContains.foreach { setMinContains(_) }
    schema.maxContains.foreach { setMaxContains(_) }
    schema.uniqueItems.foreach { setUniqueItems(_) }
    schema.additionalItems.foreach { setAdditionalItems(_) }
    schema.unevaluatedItems.foreach { setUnevaluatedItems(_) }
    schema.propertyNames.foreach { setPropertyNames(_) }
    setPatternProperties(schema.patternProperties)
    setProperties(schema.properties)
    schema.minProperties.foreach { setMinProperties(_) }
    schema.maxProperties.foreach { setMaxProperties(_) }
    schema.required.foreach { setRequired(_) }
    setDependentRequired(schema.dependentRequired)
    setDependentSchemas(schema.dependentSchemas)
    schema.additionalProperties.foreach { setAdditionalProperties(_) }
    schema.unevaluatedProperties.foreach { setUnevaluatedProperties(_) }
    schema.allOf.foreach { setAllOf(_) }
    schema.anyOf.foreach { setAnyOf(_) }
    schema.oneOf.foreach { setOneOf(_) }
    schema.not.foreach { setNot(_) }
    schema.ifApply.foreach { setIfApply(_) }
    schema.thenApply.foreach { setThenApply(_) }
    schema.elseApply.foreach { setElseApply(_) }
    setExampleValues(schema.exampleValues)
    schema.readOnly.foreach { setReadOnly(_) }
    schema.writeOnly.foreach { setWriteOnly(_) }
    schema.deprecated.foreach { setDeprecated(_) }

  /**
   * Sets schema dialect.
   *
   * @return this builder
   */
  def setSchema(value: DialectParam): this.type =
    $schema = Option(value)
    this

  /**
   * Sets schema identifier.
   *
   * @return this builder
   */
  def setId(value: URIParam): this.type =
    $id = Option(value)
    this

  /**
   * Sets dynamic schema reference URI.
   *
   * @return this builder
   */
  def setDynamicRef(value: URIParam): this.type =
    $dynamicRef = Option(value)
    this

  /**
   * Sets dynamic anchor.
   *
   * @return this builder
   */
  def setDynamicAnchor(value: String): this.type =
    $dynamicAnchor = Option(value)
    this

  /**
   * Sets vocabulary.
   *
   * @return this builder
   */
  def setVocabulary(value: Map[String, Boolean]): this.type =
    $vocabulary = value
    this

  /**
   * Sets vocabulary.
   *
   * @return this builder
   */
  def setVocabulary(value: (String, Boolean), more: (String, Boolean)*): this.type =
    setVocabulary((value +: more).toMap)

  /**
   * Adds one or more vocabulary.
   *
   * @return this builder
   */
  def addVocabulary(value: (String, Boolean), more: (String, Boolean)*): this.type =
    setVocabulary($vocabulary ++ (value +: more))

  /**
   * Sets schema reference URI.
   *
   * @return this builder
   */
  def setRef(value: URIParam): this.type =
    $ref = Option(value)
    this

  /**
   * Sets anchor.
   *
   * @return this builder
   */
  def setAnchor(value: String): this.type =
    $anchor = Option(value)
    this

  /**
   * Sets comments.
   *
   * @return this builder
   */
  def setComments(value: String): this.type =
    $comments = Option(value)
    this

  /**
   * Sets schema definitions.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setDefs(value: Map[String, JsonSchemaParam]): this.type =
    $defs = value
    this

  /**
   * Sets schema definitions.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setDefs(value: (String, JsonSchemaParam), more: (String, JsonSchemaParam)*): this.type =
    setDefs((value +: more).toMap)

  /**
   * Adds one or more schema definitions.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def addDefs(value: (String, JsonSchemaParam), more: (String, JsonSchemaParam)*): this.type =
    setDefs($defs ++ (value +: more))

  /**
   * Sets title.
   *
   * @return this builder
   */
  def setTitle(value: String): this.type =
    title = Option(value)
    this

  /**
   * Sets description.
   *
   * @return this builder
   */
  def setDescription(value: String): this.type =
    description = Option(value)
    this

  /**
   * Sets data types.
   *
   * @return this builder
   */
  def setKind(value: Seq[DataTypeParam]): this.type =
    kind = value
    this

  /**
   * Sets data types.
   *
   * @return this builder
   */
  def setKind(value: DataTypeParam, more: DataTypeParam*): this.type =
    setKind(value +: more)

  /**
   * Adds one or more data types.
   *
   * @return this builder
   */
  def addKind(value: DataTypeParam, more: DataTypeParam*): this.type =
    setKind(kind ++ (value +: more))


  /**
   * Sets default value.
   *
   * @return this builder
   */
  def setDefaultValue(value: JsonValueParam): this.type =
    defaultValue = Option(value)
    this

  /**
   * Sets constant value.
   *
   * @return this builder
   */
  def setConstValue(value: JsonValueParam): this.type =
    constValue = Option(value)
    this

  /**
   * Sets enum values.
   *
   * @return this builder
   */
  def setEnumValues(value: Seq[JsonValueParam]): this.type =
    enumValues = Option(value)
    this

  /**
   * Sets enum values.
   *
   * @return this builder
   */
  def setEnumValues(value: JsonValueParam, more: JsonValueParam*): this.type =
    setEnumValues(value +: more)

  /**
   * Adds one or more enum values.
   *
   * @return this builder
   */
  def addEnumValues(value: JsonValueParam, more: JsonValueParam*): this.type =
    setEnumValues(enumValues.getOrElse(Nil) ++ (value +: more))

  /**
   * Sets minimum length.
   *
   * @return this builder
   *
   * @note For string type.
   */
  def setMinLength(value: Int): this.type =
    minLength = Option(value)
    this

  /**
   * Sets maximum length.
   *
   * @return this builder
   *
   * @note For string type.
   */
  def setMaxLength(value: Int): this.type =
    maxLength = Option(value)
    this

  /**
   * Sets format.
   *
   * @return this builder
   *
   * @note For string type.
   */
  def setFormat(value: String): this.type =
    format = Option(value)
    this

  /**
   * Sets pattern.
   *
   * @return this builder
   *
   * @note For string type.
   */
  def setPattern(value: String): this.type =
    pattern = Option(value)
    this

  /**
   * Sets content media type.
   *
   * @return this builder
   *
   * @note For string type.
   */
  def setContentMediaType(value: String): this.type =
    contentMediaType = Option(value)
    this

  /**
   * Sets content media encoding.
   *
   * @return this builder
   *
   * @note For string type.
   */
  def setContentMediaEncoding(value: EncodingParam): this.type =
    contentMediaEncoding = Option(value)
    this

  /**
   * Sets content schema.
   *
   * @return this builder
   *
   * @note For string type.
   */
  def setContentSchema(value: JsonSchemaParam): this.type =
    contentSchema = Option(value)
    this

  /**
   * Sets minimum value.
   *
   * @return this builder
   *
   * @note For number type.
   */
  def setMinimum(value: BigDecimal): this.type =
    minimum = Option(value)
    this

  /**
   * Sets exclusive minimum value.
   *
   * @return this builder
   *
   * @note For number type.
   */
  def setExclusiveMinimum(value: BigDecimal): this.type =
    exclusiveMinimum = Option(value)
    this

  /**
   * Sets maximum value.
   *
   * @return this builder
   *
   * @note For number type.
   */
  def setMaximum(value: BigDecimal): this.type =
    maximum = Option(value)
    this

  /**
   * Sets exclusive maximum value.
   *
   * @return this builder
   *
   * @note For number type.
   */
  def setExclusiveMaximum(value: BigDecimal): this.type =
    exclusiveMaximum = Option(value)
    this

  /**
   * Sets minimum value.
   *
   * @return this builder
   *
   * @note For number type.
   */
  def setMultipleOf(value: BigDecimal): this.type =
    multipleOf = Option(value)
    this

  /**
   * Sets prefix items.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setPrefixItems(value: Seq[JsonSchemaParam]): this.type =
    prefixItems = Option(value)
    this

  /**
   * Sets prefix items.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setPrefixItems(value: JsonSchemaParam, more: JsonSchemaParam*): this.type =
    setPrefixItems(value +: more)

  /**
   * Adds one or more prefix items.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def addPrefixItems(value: JsonSchemaParam, more: JsonSchemaParam*): this.type =
    setPrefixItems(prefixItems.getOrElse(Nil) ++ (value +: more))

  /**
   * Sets items.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setItems(value: JsonSchemaParam): this.type =
    items = Option(value)
    this

  /**
   * Sets minimum number of items.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setMinItems(value: Int): this.type =
    minItems = Option(value)
    this

  /**
   * Sets maximum number of items.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setMaxItems(value: Int): this.type =
    maxItems = Option(value)
    this

  /**
   * Sets contains.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setContains(value: JsonSchemaParam): this.type =
    contains = Option(value)
    this

  /**
   * Sets minimum number of contains.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setMinContains(value: Int): this.type =
    minContains = Option(value)
    this

  /**
   * Sets maximum number of contains.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setMaxContains(value: Int): this.type =
    maxContains = Option(value)
    this

  /**
   * Sets unique items indicator.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setUniqueItems(value: Boolean): this.type =
    uniqueItems = Option(value)
    this

  /**
   * Sets additional items indicator.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setAdditionalItems(value: JsonSchemaParam): this.type =
    additionalItems = Option(value)
    this

  /**
   * Sets unevaluated items indicator.
   *
   * @return this builder
   *
   * @note For array type.
   */
  def setUnevaluatedItems(value: JsonSchemaParam): this.type =
    unevaluatedItems = Option(value)
    this

  /**
   * Sets schema for property names.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setPropertyNames(value: JsonSchemaParam): this.type =
    propertyNames = Option(value)
    this

  /**
   * Sets pattern properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setPatternProperties(value: Map[String, JsonSchemaParam]): this.type =
    patternProperties = value
    this

  /**
   * Sets pattern properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setPatternProperties(value: (String, JsonSchemaParam), more: (String, JsonSchemaParam)*): this.type =
    setPatternProperties((value +: more).toMap)

  /**
   * Adds one or more pattern properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def addPatternProperties(value: (String, JsonSchemaParam), more: (String, JsonSchemaParam)*): this.type =
    setPatternProperties(patternProperties ++ (value +: more))

  /**
   * Sets properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setProperties(value: Map[String, JsonSchemaParam]): this.type =
    properties = value
    this

  /**
   * Sets properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setProperties(value: (String, JsonSchemaParam), more: (String, JsonSchemaParam)*): this.type =
    setProperties((value +: more).toMap)

  /**
   * Adds one or more properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def addProperties(value: (String, JsonSchemaParam), more: (String, JsonSchemaParam)*): this.type =
    setProperties(properties ++ (value +: more))

  /**
   * Sets minimum number of properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setMinProperties(value: Int): this.type =
    minProperties = Option(value)
    this

  /**
   * Sets maximum number of properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setMaxProperties(value: Int): this.type =
    maxProperties = Option(value)
    this

  /**
   * Sets names of required properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setRequired(value: Seq[String]): this.type =
    required = Option(value)
    this

  /**
   * Sets names of required properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setRequired(value: String, more: String*): this.type =
    setRequired(value +: more)

  /**
   * Adds one or more names of required properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def addRequired(value: String, more: String*): this.type =
    setRequired(required.getOrElse(Nil) ++ (value +: more))

  /**
   * Sets dependent required properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setDependentRequired(value: Map[String, Seq[String]]): this.type =
    dependentRequired = value
    this

  /**
   * Sets dependent required properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setDependentRequired(value: (String, Seq[String]), more: (String, Seq[String])*): this.type =
    setDependentRequired((value +: more).toMap)

  /**
   * Adds one or more dependent required properties.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def addDependentRequired(value: (String, Seq[String]), more: (String, Seq[String])*): this.type =
    setDependentRequired(dependentRequired ++ (value +: more))

  /**
   * Sets dependent required schemas.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setDependentSchemas(value: Map[String, Seq[JsonSchemaParam]]): this.type =
    dependentSchemas = value
    this

  /**
   * Sets dependent required schemas.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setDependentSchemas(value: (String, Seq[JsonSchemaParam]), more: (String, Seq[JsonSchemaParam])*): this.type =
    setDependentSchemas((value +: more).toMap)

  /**
   * Adds one or more dependent required schemas.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def addDependentSchema(value: (String, Seq[JsonSchemaParam]), more: (String, Seq[JsonSchemaParam])*): this.type =
    setDependentSchemas(dependentSchemas ++ (value +: more))

  /**
   * Sets additional properties indicator.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setAdditionalProperties(value: JsonSchemaParam): this.type =
    additionalProperties = Option(value)
    this

  /**
   * Sets unevaluated properties indicator.
   *
   * @return this builder
   *
   * @note For object type.
   */
  def setUnevaluatedProperties(value: JsonSchemaParam): this.type =
    unevaluatedProperties = Option(value)
    this

  /**
   * Sets any-of schemas.
   *
   * @return this builder
   */
  def setAllOf(value: Seq[JsonSchemaParam]): this.type =
    allOf = Option(value)
    this

  /**
   * Sets any-of schemas.
   *
   * @return this builder
   */
  def setAllOf(value: JsonSchemaParam, more: JsonSchemaParam*): this.type =
    setAllOf(value +: more)

  /**
   * Adds one or more any-of schemas.
   *
   * @return this builder
   */
  def addAllOf(value: JsonSchemaParam, more: JsonSchemaParam*): this.type =
    setAllOf(allOf.getOrElse(Nil) ++ (value +: more))

  /**
   * Sets any-of schemas.
   *
   * @return this builder
   */
  def setAnyOf(value: Seq[JsonSchemaParam]): this.type =
    anyOf = Option(value)
    this

  /**
   * Sets any-of schemas.
   *
   * @return this builder
   */
  def setAnyOf(value: JsonSchemaParam, more: JsonSchemaParam*): this.type =
    setAnyOf(value +: more)

  /**
   * Adds one or more any-of schemas.
   *
   * @return this builder
   */
  def addAnyOf(value: JsonSchemaParam, more: JsonSchemaParam*): this.type =
    setAnyOf(anyOf.getOrElse(Nil) ++ (value +: more))

  /**
   * Sets one-of schemas.
   *
   * @return this builder
   */
  def setOneOf(value: Seq[JsonSchemaParam]): this.type =
    oneOf = Option(value)
    this

  /**
   * Sets one-of schemas.
   *
   * @return this builder
   */
  def setOneOf(value: JsonSchemaParam, more: JsonSchemaParam*): this.type =
    setOneOf(value +: more)

  /**
   * Adds one or more one-of schemas.
   *
   * @return this builder
   */
  def addOneOf(value: JsonSchemaParam, more: JsonSchemaParam*): this.type =
    setOneOf(oneOf.getOrElse(Nil) ++ (value +: more))

  /**
   * Sets not schema.
   *
   * @return this builder
   */
  def setNot(value: JsonSchemaParam): this.type =
    not = Option(value)
    this

  /**
   * Sets if conditional schema.
   *
   * @return this builder
   */
  def setIfApply(value: JsonSchemaParam): this.type =
    ifApply = Option(value)
    this

  /**
   * Sets then conditional schema.
   *
   * @return this builder
   */
  def setThenApply(value: JsonSchemaParam): this.type =
    thenApply = Option(value)
    this

  /**
   * Sets else conditional schema.
   *
   * @return this builder
   */
  def setElseApply(value: JsonSchemaParam): this.type =
    elseApply = Option(value)
    this

  /**
   * Sets example values.
   *
   * @return this builder
   */
  def setExampleValues(value: Seq[JsonValueParam]): this.type =
    exampleValues = value
    this

  /**
   * Sets example values.
   *
   * @return this builder
   */
  def setExampleValues(value: JsonValueParam, more: JsonValueParam*): this.type =
    setExampleValues(value +: more)

  /**
   * Adds one or more example values.
   *
   * @return this builder
   */
  def addExampleValues(value: JsonValueParam, more: JsonValueParam*): this.type =
    setExampleValues(exampleValues ++ (value +: more))

  /**
   * Sets read-only indicator.
   *
   * @return this builder
   */
  def setReadOnly(value: Boolean): this.type =
    readOnly = Option(value)
    this

  /**
   * Sets write-only indicator.
   *
   * @return this builder
   */
  def setWriteOnly(value: Boolean): this.type =
    writeOnly = Option(value)
    this

  /**
   * Sets deprecated indicator.
   *
   * @return this builder
   */
  def setDeprecated(value: Boolean): this.type =
    deprecated = Option(value)
    this

  /**
   * Resets builder to default values.
   *
   * @return this builder
   */
  def reset(): this.type =
    $schema               = None
    $id                   = None
    $dynamicRef           = None
    $dynamicAnchor        = None
    $vocabulary           = Map.empty
    $ref                  = None
    $anchor               = None
    $comments             = None
    title                 = None
    description           = None
    kind                  = Nil
    defaultValue          = None
    constValue            = None
    enumValues            = None
    minLength             = None
    maxLength             = None
    format                = None
    pattern               = None
    contentMediaType      = None
    contentMediaEncoding  = None
    contentSchema         = None
    minimum               = None
    exclusiveMinimum      = None
    maximum               = None
    exclusiveMaximum      = None
    multipleOf            = None
    prefixItems           = None
    items                 = None
    minItems              = None
    maxItems              = None
    contains              = None
    minContains           = None
    maxContains           = None
    uniqueItems           = None
    additionalItems       = None
    unevaluatedItems      = None
    propertyNames         = None
    patternProperties     = Map.empty
    properties            = Map.empty
    minProperties         = None
    maxProperties         = None
    required              = None
    dependentRequired     = Map.empty
    dependentSchemas      = Map.empty
    additionalProperties  = None
    unevaluatedProperties = None
    allOf                 = None
    anyOf                 = None
    oneOf                 = None
    not                   = None
    ifApply               = None
    thenApply             = None
    elseApply             = None
    exampleValues         = Nil
    readOnly              = None
    writeOnly             = None
    deprecated            = None
    $defs                 = Map.empty
    this

  /**
   * Creates JSON schema using current settins.
   *
   * @return JSON schema.
   */
  def toJsonSchema(): JsonSchema =
    ObjectJsonSchema(
      $schema               = $schema.map(ToDialect),
      $id                   = $id.map(ToURI),
      $dynamicRef           = $dynamicRef.map(ToURI),
      $dynamicAnchor        = $dynamicAnchor,
      $vocabulary           = $vocabulary,
      $ref                  = $ref.map(ToURI),
      $anchor               = $anchor,
      $comments             = $comments,
      $defs                 = $defs.map((name, schema) => name -> ToJsonSchema(schema)),
      title                 = title,
      description           = description,
      kind                  = kind.map(ToDataType),
      defaultValue          = defaultValue.map(ToJsonValue),
      constValue            = constValue.map(ToJsonValue),
      enumValues            = enumValues.map(_.map(ToJsonValue)),
      minLength             = minLength,
      maxLength             = maxLength,
      format                = format,
      pattern               = pattern,
      contentMediaType      = contentMediaType,
      contentMediaEncoding  = contentMediaEncoding.map(ToEncoding),
      contentSchema         = contentSchema.map(ToJsonSchema),
      minimum               = minimum,
      exclusiveMinimum      = exclusiveMinimum,
      maximum               = maximum,
      exclusiveMaximum      = exclusiveMaximum,
      multipleOf            = multipleOf,
      prefixItems           = prefixItems.map(_.map(ToJsonSchema)),
      items                 = items.map(ToJsonSchema),
      minItems              = minItems,
      maxItems              = maxItems,
      contains              = contains.map(ToJsonSchema),
      minContains           = minContains,
      maxContains           = maxContains,
      uniqueItems           = uniqueItems,
      additionalItems       = additionalItems.map(ToJsonSchema),
      unevaluatedItems      = unevaluatedItems.map(ToJsonSchema),
      propertyNames         = propertyNames.map(ToJsonSchema),
      patternProperties     = patternProperties.map((name, schema) => name -> ToJsonSchema(schema)),
      properties            = properties.map((name, schema) => name -> ToJsonSchema(schema)),
      minProperties         = minProperties,
      maxProperties         = maxProperties,
      required              = required,
      dependentRequired     = dependentRequired,
      dependentSchemas      = dependentSchemas.map((name, schemas) => name -> schemas.map(ToJsonSchema)),
      additionalProperties  = additionalProperties.map(ToJsonSchema),
      unevaluatedProperties = unevaluatedProperties.map(ToJsonSchema),
      allOf                 = allOf.map(_.map(ToJsonSchema)),
      anyOf                 = anyOf.map(_.map(ToJsonSchema)),
      oneOf                 = oneOf.map(_.map(ToJsonSchema)),
      not                   = not.map(ToJsonSchema),
      ifApply               = ifApply.map(ToJsonSchema),
      thenApply             = thenApply.map(ToJsonSchema),
      elseApply             = elseApply.map(ToJsonSchema),
      exampleValues         = exampleValues.map(ToJsonValue),
      readOnly              = readOnly,
      writeOnly             = writeOnly,
      deprecated            = deprecated
    )
