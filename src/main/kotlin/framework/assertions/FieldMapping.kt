package framework.assertions

data class FieldMapping(
    val requestField: String,
    val responseField: String
)

data class ModelComparisonConfig(
    val requestClass: String,
    val responseClass: String,
    val fieldMappings: List<FieldMapping>
)
