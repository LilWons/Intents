/*
    Author: LilWons
    Date: 2023-12-09

    Description: Fragment PhotoEvent uses MediaStore.ACTION_IMAGE_CAPTURE intent to access camera and take a photo.
    ImageView is updated after picture is taken with image.
    Handles permissions for CAMERA.
 */
package com.example.casestudy3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.casestudy3.databinding.FragmentPhotoEventBinding

class PhotoEvent : Fragment() {
    private lateinit var takePhoto: ActivityResultLauncher<Void?>
    private var _binding: FragmentPhotoEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Checks if necessary permissions are granted for camera
        if(!Util.permsCheck(requireContext(),Manifest.permission.CAMERA))
            Util.requestPermissions(requireActivity(), REQUEST_CAMERA_PERMISSION,arrayOf(Manifest.permission.CAMERA)) //Requests permissions if CAMERA is not granted.
        //Sets up camera.
        setupCam()
    }
    //Creates Fragment Layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        _binding = FragmentPhotoEventBinding.inflate(inflater, container, false)
        return binding.root
    }
    //Once View has been created, setups UI Event listeners.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }
    //Final clean up of fragment.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null //Sets _binding to null to avoid memory leaks.
    }
    //Handles the callback from the permission request response.
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> { //When the permission request code is for CAMERA permissions...
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { //Checks if grantResults array is empty than checks if the first permission in the request was granted.
                    takePhoto()  // Call takePhoto().
                } else {
                    Toast.makeText(requireActivity(),"Camera Permission Required!", Toast.LENGTH_SHORT).show() //Prompt user the CAMERA permissions are required to make a call with Toast.
                }
                return
            }
        }
    }
    //Creates activity result listener for taking photos with camera.
    private fun setupCam() {
        //Creates callback for receiving result from camera activity.
        takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap -> //Lambda function to set ivCamera to captured photo from camera activity.
            if (bitmap != null) { //If bitmap exists...
                binding.ivCamera.setImageBitmap(bitmap) //...update ivCamera.setImageBitmap with bitmap.
            }
        }
    }
    //Setups listeners for the PhotoEvent fragment UI.
    private fun setupListeners(){
        with(binding){
            ivCamera.setOnClickListener{
                if(Util.permsCheck(requireContext(),Manifest.permission.CAMERA)) //Checks if permissions are granted for CAMERA
                    takePhoto() // Call takePhoto().
            }
        }
    }
    //Attempts to start an activity to take a photo.
    private fun takePhoto(){
        val photoCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //Creates action intent with MediaStore.ACTION_IMAGE_CAPTURE
        if(photoCaptureIntent.resolveActivity(requireActivity().packageManager) != null){ //Check if Activity exists to handle ACTION_IMAGE_CAPTURE intent.
            takePhoto.launch(null) //Start camera app.
        }
        else{
            Toast.makeText(requireContext(), "No app found to make a photo", Toast.LENGTH_SHORT).show() //No activity is found, notify user with Toast.
        }
    }
    companion object {
        const val REQUEST_CAMERA_PERMISSION = 111 //Request code for CAMERA permissions.
        }
    }