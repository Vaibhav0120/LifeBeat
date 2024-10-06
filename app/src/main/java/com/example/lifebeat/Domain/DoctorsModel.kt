package com.example.lifebeat.Domain

import android.os.Parcel
import android.os.Parcelable

data class DoctorsModel(
    val Address: String = "",
    val Biography: String = "",
    val Id: Int = 0,
    val Name: String = "",
    val Picture: String = "",
    val Special: String = "",
    val Experience: String = "",
    val Location: String = "",
    val Mobile: String = "",
    val Patients: String = "",
    val Rating: Double = 0.0,
    val Site: String = "",
    val categoryId: Int = 0  // Add categoryId to link doctor with category
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readInt()  // Read categoryId from Parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Address)
        parcel.writeString(Biography)
        parcel.writeInt(Id)
        parcel.writeString(Name)
        parcel.writeString(Picture)
        parcel.writeString(Special)
        parcel.writeString(Experience)
        parcel.writeString(Location)
        parcel.writeString(Mobile)
        parcel.writeString(Patients)
        parcel.writeDouble(Rating)
        parcel.writeString(Site)
        parcel.writeInt(categoryId)  // Write categoryId to Parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DoctorsModel> {
        override fun createFromParcel(parcel: Parcel): DoctorsModel {
            return DoctorsModel(parcel)
        }

        override fun newArray(size: Int): Array<DoctorsModel?> {
            return arrayOfNulls(size)
        }
    }
}
