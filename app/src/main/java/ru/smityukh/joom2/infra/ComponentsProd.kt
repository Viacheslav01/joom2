package ru.smityukh.joom2.infra

class ComponentsProd : Components {

    private val mainComponents by lazy { DaggerMainComponent.create() }

    override fun getMainComponent(): MainComponent = mainComponents
}