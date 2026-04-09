package com.future.components.future.net.ui

import android.os.Bundle
import android.widget.Toast
import com.future.components.client.base.BaseActivity
import com.future.components.future.net.R
import com.future.components.future.net.databinding.ActivityMainBinding
import com.future.components.net.NetworkHelper

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(){

    override fun initData(saveInstanceState: Bundle?) {
        viewBinding.vm = viewModel
        NetworkHelper.INSTANCE.setDebug(true)
        viewModel.getConfigData()
        viewModel.getStentsGetAll()
        viewModel.getCollisionGetInfo()
        viewBinding.btnRefresh.setOnClickListener { viewModel.getConfigData() }
        viewBinding.add.setOnClickListener { viewModel.stentsAdd() }
        viewBinding.delete.setOnClickListener { viewModel.stentsDelete() }
        viewBinding.refreshBracket.setOnClickListener { viewModel.getStentsGetAll() }
        viewBinding.updateCollision.setOnClickListener { viewModel.collisionUpdate() }
        viewBinding.refreshCollision.setOnClickListener { viewModel.getCollisionGetInfo() }
        viewBinding.collision.setOnClickListener { viewModel.opcIssueControl("COLLISION") }
        viewBinding.alarmBtn.setOnClickListener { viewModel.opcIssueControl("WARN") }

        viewBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.open->viewModel.saveAiRecord(1)
                R.id.close-> viewModel.saveAiRecord(0)
            }
        }
    }


    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }
}