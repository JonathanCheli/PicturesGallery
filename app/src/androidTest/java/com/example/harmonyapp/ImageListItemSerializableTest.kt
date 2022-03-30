package com.example.harmonyapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.harmonyapp.data.model.ImageListItem
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

@RunWith(AndroidJUnit4::class)
class ImageListItemSerializableTest {

    @Test
    fun test_item_is_serializable() {
        val imageItem = ImageListItem(
            id = "1", author = "author1", height = 100, width = 100, url = "https://abc.com",
            download_url = "https://abc.com", page = 1
        )
        val serializedBytes = ByteArrayOutputStream().use { byteArrayOutputStream ->
            ObjectOutputStream(byteArrayOutputStream).use {
                it.writeObject(imageItem)
            }
            byteArrayOutputStream.toByteArray()
        }

        val result = ByteArrayInputStream(serializedBytes).use {
            ObjectInputStream(it).use {
                it.readObject()
            }
        }
        assertThat(result).isEqualTo(imageItem)
    }
}