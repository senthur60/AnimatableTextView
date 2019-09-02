package com.senthur.animatabletextview

class TextAnimationProperties(
    var scaleX: Float = 1.0f,
    var scaleY: Float = 1f,
    var transX: Float = 0f,
    var transY: Float = 0f,
    var rotate: Float = 0f,
    var alpha: Float = 1f,
    var animType: AnimType = AnimType.PLAY_WITH_DELAY,
    var duration : Float =1f
){
    enum class AnimType{
        PLAY_SEQUENTIALLY,
        PLAY_WITH_DELAY,
        PLAY_TOGETHER
    }
}