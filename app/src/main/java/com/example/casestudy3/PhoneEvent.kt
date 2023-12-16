/*
    Author: LilWons
    Date: 2023-12-09

    Description: Fragment PhoneEvent gets phone number and/or SMS message from user.
    Uses Intent.ACTION_VIEW to send a SMS message with the message activity.
    Uses Intent.ACTION_CALL to make a phone call with the phone activity.
    Handles permissions for CALL_PHONE and SEND_SMS.
 */
package com.example.casestudy3

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.casestudy3.databinding.FragmentPhoneEventBinding

class PhoneEvent : Fragment() {

    private var _binding: FragmentPhoneEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!Util.permsCheck(requireContext(),Manifest.permission.CALL_PHONE)) //Checks if necessary permissions are granted to make phone calls.
            Util.requestPermissions(requireActivity(),REQUEST_PHONE_PERMISSION, arrayOf(Manifest.permission.CALL_PHONE)) //Requests permissions if ACTION_CALL is not granted.
        else if(!Util.permsCheck(requireContext(),Manifest.permission.SEND_SMS))//Checks if necessary permissions are granted to send SMS message.
            Util.requestPermissions(requireActivity(),REQUEST_SMS_PERMISSION, arrayOf(Manifest.permission.SEND_SMS)) //Requests permissions if SEND_SMS is not granted.
    }
    //Creates Fragment Layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneEventBinding.inflate(inflater, container, false)
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
    //Gets phone number and places phone call.
    private fun placeCall() {
        val phoneNumber = binding.etPhoneNumber.text.toString() //Gets phone number from etPhoneNumber.text and converts to string.
        val callIntent = Intent(Intent.ACTION_CALL).apply {//Creates action intent with Intent.ACTION.CALL
            data = Uri.parse("tel:$phoneNumber") //Sets URI scheme for telephone numbers with phoneNumber.
        }
        //Checks if CALL_PHONE is granted...
        if (Util.permsCheck(requireContext(),Manifest.permission.CALL_PHONE)) {
            //...try to start the Activity that can handle the Intent.ACTION_CALL action for a phone call.
            try {
                startActivity(callIntent)
            } catch (e: ActivityNotFoundException) {
                //If no activity is found, notify user with Toast.
                Toast.makeText(requireContext(), "No app found to make a call", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Gets phone number and SMS Message than attempts to sends SMS message.
    private fun sendSMS() {
        val phoneNumber = binding.etPhoneNumber.text.toString() //Gets phone number from etPhoneNumber.text and converts to string.
        val smsMessage = binding.etSMS.text.toString() //Gets SMS message from etSMS.text and converts to string.
        val smsIntent = Intent(Intent.ACTION_VIEW).apply {//Creates action intent with Intent.ACTION_VIEW.
            data = Uri.parse("smsto:$phoneNumber") //Sets URI scheme for sending SMS message.
            putExtra("sms_body", smsMessage)
        }
        //Checks if CALL_PHONE is granted...
        if (Util.permsCheck(requireContext(),Manifest.permission.SEND_SMS)) {
            //...try to start the Activity that can handle the Intent.ACTION_VIEW action to send SMS Message.
            try {
                startActivity(smsIntent)
            } catch (e: ActivityNotFoundException) {
                //If no activity is found, notify user with Toast.
                Toast.makeText(requireContext(), "No app found to make a call", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Handles the callback from the permission request response.
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PHONE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { //Check if permission was granted.
                    placeCall() //If permission was granted, try to place the call by calling placeCall().
                } else {
                    Toast.makeText(requireActivity(),"Phone Permission Required!", Toast.LENGTH_SHORT).show() //Prompt user the phone permissions are required to make a call with Toast.
                }
            }
            REQUEST_SMS_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { //Check if permission was granted.
                    sendSMS() //If permission was granted, try to send SMS by calling sendSMS().
                } else {
                    Toast.makeText(requireActivity(),"SMS Permission Required!", Toast.LENGTH_SHORT).show() //Prompt user the SEND_SMS permissions are required to make a send a SMS with Toast.
                }
            }
        }
    }
    //Setups listeners for the PhoneEvent fragment UI.
    private fun setupListeners(){
        with(binding){
            ivPhone.setOnClickListener {
                //Check if CALL_PHONE permissions are granted to make call.
                if (Util.permsCheck(requireContext(),Manifest.permission.CALL_PHONE)) {
                    placeCall() //If CALL_PHONE permission are granted, try to place the call by calling placeCall().
                } else {
                    //requestPhonePermissions() //If CALL_PHONE permission are not granted, request permissions.
                    Util.requestPermissions(requireActivity(),REQUEST_PHONE_PERMISSION, arrayOf(Manifest.permission.CALL_PHONE))
                }
            }
            ivSMS.setOnClickListener{
                //Check if SEND_SMS permissions are granted to make call.
                //if (permsSMSCheck()) {
                if (Util.permsCheck(requireContext(),Manifest.permission.SEND_SMS)) {
                    sendSMS() //If SEND_SMS permission are granted, try to send SMS message by calling sendSMS().
                } else {
                    //requestSMSPermissions() //If SEND_SMS permission are not granted, request permissions.
                    Util.requestPermissions(requireActivity(),REQUEST_SMS_PERMISSION, arrayOf(Manifest.permission.SEND_SMS))
                }
            }
        }
    }
    companion object {
        const val REQUEST_PHONE_PERMISSION = 112 //Request code for CALL_PHONE permissions.
        const val REQUEST_SMS_PERMISSION = 113 //Request code for SEND_SMS permissions.
    }
}