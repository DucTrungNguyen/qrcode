package com.example.qrcode

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.qrcode.Model.QRGeoModel
import com.example.qrcode.Model.QRUrlModel
import com.example.qrcode.Model.QRVCardModel
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Yeu cau permission
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    zxscan.setResultHandler(this@MainActivity)
                    zxscan.startCamera()

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@MainActivity, "Can cap quyen", Toast.LENGTH_SHORT).show()
                }

            } ).check()
    }

    override fun handleResult(rawResult: Result?) {
        processRawResult(rawResult!!.text)

        txt_result.text = rawResult!!.text
    }

    fun processRawResult(text:String?)
    {
        if(text!!.startsWith("BEGIN"))
        {
            val tokens = text!!.split("\n".toRegex()).dropLastWhile ({ it.isEmpty() }).toTypedArray()
            val qrcardmodel = QRVCardModel()
            for ( i in tokens.indices){
                if ( tokens[i].startsWith("BEGIN"))
                    qrcardmodel.type = tokens[i].substring("BEGIN".length)
                else if (tokens[i].startsWith("N:"))
                    qrcardmodel.name = tokens[i].substring("N:".length)
                else if (tokens[i].startsWith("ORG:"))
                    qrcardmodel.org = tokens[i].substring("ORG:".length)
                else if ( tokens[i].startsWith("TEL"))
                    qrcardmodel.tel = tokens[i].substring("TEL".length)
                else if ( tokens[i].startsWith("URL:"))
                    qrcardmodel.url= tokens[i].substring(("URL:").length)
                else if ( tokens[i].startsWith("EMAIL:"))
                    qrcardmodel.email = tokens[i].substring("EMAIL:".length)
                else if ( tokens[i].startsWith("ADR:"))
                    qrcardmodel.address = tokens[i].substring("ADR:".length)
                else  if ( tokens[i].startsWith("NOTE:"))
                    qrcardmodel.note = tokens[i].substring("NOTE:".length)
                else if ( tokens[i].startsWith("SUMMARY:"))
                    qrcardmodel.summary = tokens[i].substring("SUMMARY".length)
                else if ( tokens[i].startsWith("DTSTART"))
                    qrcardmodel.dtstart = tokens[i].substring("DTSTART:".length)
                else if ( tokens[i].startsWith("DTEND"))
                    qrcardmodel.dtend = tokens[i].substring("DTEND".length)


                if (qrcardmodel.type.equals("VCARD"))
                    txt_result!!.text= qrcardmodel.name
                else
                    txt_result!!.text = qrcardmodel.type
            }
        }
        else
            if ( text!!.startsWith("https://")
            || text!!.startsWith("http://")
            || text!!.startsWith("www."))
        {
            val qrurl = QRUrlModel()
            qrurl.url = text!!
            txt_result!!.text = qrurl.url

        }
        else
                if( text!!.startsWith("geo"))
            {
                val qrGeo = QRGeoModel()
                val delims = "[ , ?q= ]+"
                val  tokens = text.split(delims.toRegex()).dropLastWhile ({ it.isEmpty() }).toTypedArray()

                for ( i in tokens.indices)
                {
                    if ( tokens[i].startsWith("geo"))
                    {
                        qrGeo.lat = tokens[i].substring("geo".length)

                    }
                    qrGeo.lat = tokens[0].substring("geo".length)
                    qrGeo.lng = tokens[1].substring("geo".length)
                    qrGeo.geo_place = tokens[2].substring("geo".length)

                    txt_result.text = qrGeo.lat + "/" + qrGeo.lng
                }
            }
        else
                    txt_result.text = text!!
    }
}
