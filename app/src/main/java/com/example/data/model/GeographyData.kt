package com.example.data.model

object GeographyData {
    val divisionsWithDistricts = mapOf(
        "Dhaka" to listOf("Dhaka", "Gazipur", "Narayanganj", "Tangail", "Faridpur", "Manikganj", "Munshiganj", "Narsingdi", "Kishoreganj", "Gopalganj", "Madaripur", "Rajbari", "Shariatpur"),
        "Chattogram" to listOf("Chattogram", "Cox's Bazar", "Cumilla", "Feni", "Brahmanbaria", "Noakhali", "Chandpur", "Lakshmipur", "Rangamati", "Khagrachhari", "Bandarban"),
        "Rajshahi" to listOf("Rajshahi", "Bogura", "Pabna", "Sirajganj", "Naogaon", "Natore", "Chapai Nawabganj", "Joypurhat"),
        "Khulna" to listOf("Khulna", "Jashore", "Kushtia", "Satkhira", "Jhenaidah", "Bagerhat", "Chuadanga", "Magura", "Meherpur", "Narail"),
        "Barishal" to listOf("Barishal", "Patuakhali", "Bhola", "Pirojpur", "Barguna", "Jhalokathi"),
        "Sylhet" to listOf("Sylhet", "Moulvibazar", "Habiganj", "Sunamganj"),
        "Rangpur" to listOf("Rangpur", "Dinajpur", "Kurigram", "Gaibandha", "Nilphamari", "Lalmonirhat", "Thakurgaon", "Panchagarh"),
        "Mymensingh" to listOf("Mymensingh", "Jamalpur", "Netrokona", "Sherpur")
    )

    val divisions = divisionsWithDistricts.keys.toList()

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    val institutionTypes = listOf("Bangla Medium", "English Medium", "English Version", "Madrasah", "Cadet College")

    val classes = listOf("Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8", "Class 9", "Class 10")

    fun getCategoryForClass(className: String): String {
        return when (className) {
            "Class 3", "Class 4", "Class 5" -> "PRIMARY"
            "Class 6", "Class 7", "Class 8" -> "JUNIOR"
            "Class 9", "Class 10" -> "SECONDARY"
            else -> "SPECIAL"
        }
    }
}
