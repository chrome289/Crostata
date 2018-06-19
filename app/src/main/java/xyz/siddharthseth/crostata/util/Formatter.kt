package xyz.siddharthseth.crostata.util

/**
 * formatter class for misc things
 */
class Formatter {
    companion object {
        /**
         * format currency in US format
         * @param amount the amount
         * @return 123456789 -> P$ 123,456,789
         */
        internal fun currencyCommaSeparated(amount: Long): String {
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