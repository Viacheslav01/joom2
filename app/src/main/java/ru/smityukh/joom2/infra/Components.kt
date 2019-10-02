package ru.smityukh.joom2.infra

interface Components {

    fun getMainComponent(): MainComponent

    object Current : Components {

        private lateinit var instance: Components

        fun init(components: Components) {
            instance = components
        }

        override fun getMainComponent(): MainComponent = instance.getMainComponent()
    }
}