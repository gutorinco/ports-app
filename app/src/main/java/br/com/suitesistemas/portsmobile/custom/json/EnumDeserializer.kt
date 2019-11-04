package br.com.suitesistemas.portsmobile.custom.json

import br.com.suitesistemas.portsmobile.model.enums.ECustomerSituation
import br.com.suitesistemas.portsmobile.model.enums.EFinancialReleaseStatus
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class ECustomerSituationDeserializer : JsonDeserializer<ECustomerSituation>() {

    override fun deserialize(jsonParser: JsonParser?, ctxt: DeserializationContext?): ECustomerSituation {
        val text = jsonParser?.text ?: ""
        return if (text.isEmpty()) ECustomerSituation.A else ECustomerSituation.valueOf(text)
    }

}

class EFinancialReleaseStatusDeserializer : JsonDeserializer<EFinancialReleaseStatus>() {

    override fun deserialize(jsonParser: JsonParser?, ctxt: DeserializationContext?): EFinancialReleaseStatus {
        val text = jsonParser?.text ?: ""
        return if (text.isEmpty()) EFinancialReleaseStatus.A else EFinancialReleaseStatus.valueOf(text)
    }
}