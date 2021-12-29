package com.example.in2000_team41.api.metalerts

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

private val ns: String? = null

class AlertParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): AlertModel {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readAlert(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readAlert(parser: XmlPullParser): AlertModel {
        var id: String? = null
        var sender: String? = null
        var sent: String? = null
        var status: String? = null
        var msgType: String? = null
        var scope: String? = null
        var info: AlertInfo? = null

        parser.require(XmlPullParser.START_TAG, ns, "alert")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "identifier" -> id = readAttribute(parser, parser.name)
                "sender" -> sender = readAttribute(parser, parser.name)
                "sent" -> sent = readAttribute(parser, parser.name)
                "status" -> status = readAttribute(parser, parser.name)
                "scope" -> scope = readAttribute(parser, parser.name)
                "msgType" -> msgType = readAttribute(parser, parser.name)
                //"code" -> ..
                "info" -> {info = readInfo(parser)
                    break
                }

                else -> skip(parser)
            }
        }
        return AlertModel(null, id, sender, sent, status, msgType, scope, info)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readInfo(parser: XmlPullParser): AlertInfo {
        val info: AlertInfo?
        var area: Area? = null
        var event: String? = null
        var severity: String? = null
        var urgency: String? = null
        var certainty: String? = null
        var effective: String? = null
        var onset: String? = null
        var expires: String? = null
        var headline: String? = null
        var description: String? = null
        var instruction: String? = null


        val paraList = mutableMapOf<String, String>()

        parser.require(XmlPullParser.START_TAG, ns, "info")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "event" -> event = readAttribute(parser, parser.name)
                "severity" -> severity = readAttribute(parser, parser.name)
                "urgency" -> urgency = readAttribute(parser, parser.name)
                "certainty" -> certainty = readAttribute(parser, parser.name)
                "effective" -> effective = readAttribute(parser, parser.name)
                "onset" -> onset = readAttribute(parser, parser.name)
                "expires" -> expires = readAttribute(parser, parser.name)
                "headline" -> headline = readAttribute(parser, parser.name)
                "description" -> description = readAttribute(parser, parser.name)
                "instruction" -> instruction = readAttribute(parser, parser.name)
                //"category" ->
                // "senderName" ->
                //"web" ->
                //"contact" ->
                "parameter" -> {
                    val pair = readKeyValuePair(parser, "parameter")
                    paraList.put(pair[0], pair[1])
                }
                "area" -> {
                    area = readArea(parser)
                }
                else -> skip(parser)
            }
        }
        info = AlertInfo(event, severity, urgency, certainty, effective, onset, expires, headline, description, instruction, paraList, area)
        return info
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readKeyValuePair(parser: XmlPullParser, tag: String): Array<String> {
        val keyValuePair: Array<String> = arrayOf<String>("","")

        parser.require(XmlPullParser.START_TAG, ns, tag)

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "valueName" -> keyValuePair.set(0, readAttribute(parser, parser.name))
                "value" -> keyValuePair.set(1, readAttribute(parser, parser.name))
                else -> skip(parser)
            }
        }
        return keyValuePair
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readArea(parser: XmlPullParser): Area? {
        val area: Area?
        var geocode: Array<String> = arrayOf<String>("","")
        var areaDesc: String? = null
        var polygon: String? = null
        var altitude: String? = null
        var ceiling: String? = null

        parser.require(XmlPullParser.START_TAG, ns, "area")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "areaDesc" -> areaDesc = readAttribute(parser, parser.name)
                "polygon" -> polygon = readAttribute(parser, parser.name)
                "altitude" -> altitude = readAttribute(parser, parser.name)
                "ceiling" -> ceiling = readAttribute(parser, parser.name)
                /*
                "geocode" -> {
                    geocode = readKeyValuePair(parser, "geocode")
                }

                */
                else -> skip(parser)
            }
        }
        area = Area(areaDesc, polygon, altitude, ceiling)
        return area
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readAttribute(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val value = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return value
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}

