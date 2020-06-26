package com.freezer.mathsolver

import android.app.Application
import com.myscript.iink.Engine
import com.freezer.mathsolver.certificate.MyCertificate

object IInkEngine : Application() {
  fun getEngine() : Engine{
    return Engine.create(MyCertificate.getBytes())
  }
}