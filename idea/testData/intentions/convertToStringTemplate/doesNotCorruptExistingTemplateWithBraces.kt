// WITH_RUNTIME
fun main(args: Array<String>){
    val y = "cde"
    val z = "${listOf(1, 2)}" +<caret> "$y.bar"
}
