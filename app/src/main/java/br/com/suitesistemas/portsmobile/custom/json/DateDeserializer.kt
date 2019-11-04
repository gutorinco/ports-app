package br.com.suitesistemas.portsmobile.custom.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateDeserializer : JsonDeserializer<Date>() {

    override fun deserialize(jsonParser: JsonParser?, ctxt: DeserializationContext?): Date {
        val date = jsonParser?.text ?: return Date()

        return if (date.contains(":")) {
            val format = SimpleDateFormat("HH:mm:ss")
            try {
                format.parse(date)
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
        } else {
            val time = date.toLong()
            Date(time)
        }
    }

}

class DateSerializer : JsonSerializer<Date>() {

    override fun serialize(value: Date?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = format.format(value)
        gen?.writeString(date)
    }

}