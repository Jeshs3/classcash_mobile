import android.os.Parcel
import android.os.Parcelable

data class Notification(val message: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification = Notification(parcel)
        override fun newArray(size: Int): Array<Notification?> = arrayOfNulls(size)
    }
}