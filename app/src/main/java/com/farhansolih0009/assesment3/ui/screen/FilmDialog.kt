package com.farhansolih0009.assesment3.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.farhansolih0009.assesment3.R
import com.farhansolih0009.assesment3.model.Film

@Composable
fun FilmDialog(
    film: Film?, // Film yang diedit, null jika mode tambah
    bitmap: Bitmap?, // Bitmap baru yang dipilih
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    onPickImage: () -> Unit // Aksi untuk memilih gambar
) {
    var name by remember(film?.name) { mutableStateOf(film?.name ?: "") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = Modifier.clickable { onPickImage() }) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Gambar baru",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    } else if (film != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(film.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Gambar saat ini",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.loading_img),
                            error = painterResource(id = R.drawable.baseline_broken_image_24)
                        )
                    } else {
                        // Tampilkan placeholder awal jika mode tambah dan belum ada gambar dipilih
                        Image(
                            painter = painterResource(id = R.drawable.outline_animated_images_24),
                            contentDescription = "Pilih Gambar",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.5f)),
                            contentScale = ContentScale.Inside
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(id = R.string.nama_film)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.batal))
                    }
                    OutlinedButton(
                        onClick = { onConfirmation(name) },
                        enabled = name.isNotEmpty(),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.simpan))
                    }
                }
            }
        }
    }
}