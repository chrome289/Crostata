package xyz.siddharthseth.crostata.util

class CurrencyFormatter {
    companion object {

        internal fun commaSeparated(amount: Long): String {
            val str = StringBuilder(amount.toString())
            var x = amount.toString().length

            while (x > 2) {
                x -= 3
                str.insert(x, ",")
            }
            str.deleteCharAt(0)
            str.insert(0, "P$ ")
            return str.toString()
        }
    }
}