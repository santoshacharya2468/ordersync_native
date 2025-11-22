import com.google.gson.*
import kotlinx.datetime.Instant
import java.lang.reflect.Type

class KotlinxInstantAdapter : JsonDeserializer<Instant>, JsonSerializer<Instant> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Instant {
        return Instant.parse(json.asString)
    }

    override fun serialize(src: Instant, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }
}
