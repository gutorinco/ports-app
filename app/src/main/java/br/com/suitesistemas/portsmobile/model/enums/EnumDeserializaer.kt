package br.com.suitesistemas.portsmobile.model.enums

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException

class EnumDeserializer: JsonDeserializer<ECustomerSituation>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): ECustomerSituation {
        val value = jp.text
        for (enumValue in ECustomerSituation.values())
            if (enumValue.name == value)
                return enumValue
        return ECustomerSituation.A
    }

}