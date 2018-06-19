package xyz.siddharthseth.crostata.data.model

class VigilanceAction(var _id: Int, var isPositive: Boolean, var action: String) : Cloneable {

    //cloneable
    override fun clone(): VigilanceAction {
        return super.clone() as VigilanceAction
    }

    companion object {
        //deep copy list
        fun cloneList(vigilanceActionList: ArrayList<VigilanceAction>): ArrayList<VigilanceAction> {
            val newList = ArrayList<VigilanceAction>()
            for (vigilanceAction in vigilanceActionList) {
                newList.add(vigilanceAction.clone())
            }
            return newList
        }
    }
}