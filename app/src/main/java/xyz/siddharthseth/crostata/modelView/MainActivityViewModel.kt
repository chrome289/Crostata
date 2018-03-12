package xyz.siddharthseth.crostata.modelView

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import xyz.siddharthseth.crostata.R

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    var lastSelectedId = R.id.home
}