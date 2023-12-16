/*
    Author: LilWons
    Date: 2023-12-09

    Description: Fragment CalendarEvent gets allows user to create an event in the calendar application.
    Uses Intent.ACTION_INSERT to create an Event using the Calendar activity.
    Converts date and time to Epoch timestamp in milliseconds.
 */

package com.example.casestudy3

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.casestudy3.databinding.FragmentCalendarEventBinding
import java.lang.NumberFormatException
import java.util.Calendar

class CalendarEvent : Fragment() {


    private var _binding: FragmentCalendarEventBinding? = null
    private val binding get() = _binding!!


    //Creates Fragment Layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarEventBinding.inflate(inflater, container, false)
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

    //Adds passed parameters to Event on Calendar Application
    private fun addEvent(title: String, start: Long, end: Long, isAllDay: Boolean, description: String, isPrivate: Boolean, location: String) {
        Log.i(ContentValues.TAG, "isPrivate: $isPrivate ")
        //Creates an 'Intent' with the action Intent.ACTION_INSERT.
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI //Sets URI for the intent to calender event content URI.
            putExtra(CalendarContract.Events.TITLE, title) //Sets the Event title.
            putExtra(CalendarContract.Events.EVENT_LOCATION, location) //Sets Event location.
            putExtra(CalendarContract.Events.DESCRIPTION, description) //Sets Event description.
            putExtra(CalendarContract.Events.ALL_DAY, isAllDay) //Indicates if Event is an all day event.
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start) //Sets the start time and date of the event using Epoch timestamp in milliseconds.
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end) //Sets the end time and date of the event using Epoch timestamp in milliseconds.

            //If this is not a private event...
            if(!isPrivate){
                putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC) //...indicates the event is open to the public.
            }
            else{
                putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)//...indicates the event is closed to the public.
            }
        }
        //Try to start the Activity that can handle the Intent.ACTION_INSERT action for a calendar event.
        try{
            startActivity(intent)
        }
        //If no activity is found, notify user with Toast.
        catch(e:ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No app found to make a call", Toast.LENGTH_SHORT).show()
        }
    }

    //Converts parameters date (dd/MM/yy) and time (hh/MM) to Epoch timestamp in milliseconds and returns timestamp as Long type.
    private fun timeToMillis(date: String, time:String): Long {
        try {
            val timeParts = time.split(":") //Splits time on ":" delimiter into parts.
            val hours = timeParts[0].toInt() //Converts first part to hour as integer type
            val minutes = timeParts[1].toInt() //Converts second part to minute as integer type

            val dateParts = date.split("/") //Splits date on "/" delimiter into parts.
            val day = dateParts[0].toInt() //Converts first part to day as integer type
            val month =
                dateParts[1].toInt() - 1 //Converts second part to month as integer type. Subtracts 1 as month starts at 0.
            val year = dateParts[2].toInt() //Converts third part to year as integer type

        Log.i(ContentValues.TAG, "hours: $hours  min: $minutes day: $day month: $month year: $year")

        val calendar = Calendar.getInstance().apply {//Creates instance of Calendar and applies the following settings:
            set(year, month, day) //Sets year, month, and day
            set(Calendar.HOUR_OF_DAY, hours) //Sets hour.
            set(Calendar.MINUTE, minutes) //Sets minute.
            set(Calendar.SECOND, 0) //Sets seconds to zero as accuracy is not needed for Event.
            set(Calendar.MILLISECOND, 0) //Sets millisecond to zero as accuracy is not needed for Event.
        }

        return calendar.timeInMillis //Returns the value in milliseconds since the Unix Epoch (January 1, 1970, 00:00:00 GMT).
        }catch (e:NumberFormatException){
            Toast.makeText(requireActivity(),"Select Event Date and Time",Toast.LENGTH_SHORT).show()
            return 0
        }
    }

    //Sets up view listeners with FragmentCalendarEventBinding binding
    private fun setupListeners(){
        with(binding){
            //Gets Event details passes to addEvent when called.
            ivAddEvent.setOnClickListener{
                val eventTitle = etTitle.text.toString() //Gets the text from etTitle and converts to String, assigns value to eventTitle.
                val startTime = tvStartTime.text.toString() //Gets the text from tvStartTime and converts to String, assigns value to startTime.
                val endTime = tvEndTime.text.toString() //Gets the text from tvEndTime and converts to String, assigns value to endTime.
                val date = tvDate.text.toString() //Gets the text from tvDate and converts to String, assigns value to date.

                val allDay = cbAllDay.isChecked //Gets the state of cbAllDay and assigns boolean value to allDay.
                val location = etLocation.text.toString() //Gets the text from location and converts to String, assigns value to etLocation.
                val eventDisc = etDescription.text.toString() //Gets the text from etDescription and converts to String, assigns value to eventDisc.
                val accessType = swAccessType.isChecked //Gets the state of swAccessType and assigns boolean value to accessType.
                val emailList = tvEmailList.text.toString() //Gets the text from emailList and converts to String, assigns value to emailList.
                val appendEventDisc = "$eventDisc \n $emailList" //Concatenates emailList to Event Description.

                /*If the Event is all day, sets the start time and end time to reflect all day event.
                Passes event details to addEvent when called. */
                if(allDay){
                    val startEvent = timeToMillis(date, "00:00")
                    val endEvent = timeToMillis(date, "23:59")
                    addEvent(eventTitle, startEvent, endEvent, true, appendEventDisc, accessType, location)
                }
                else{
                    val startEvent = timeToMillis(date, startTime)
                    val endEvent = timeToMillis(date, endTime)
                    if(startEvent.toInt() != 0 && endEvent.toInt() != 0)
                        addEvent(eventTitle, startEvent, endEvent, false, appendEventDisc, accessType,location)
                }
            }
            //Adds email to email list.
            ivAddEmail.setOnClickListener{
                val emailList = tvEmailList.text.toString() //Gets the text from tvEmailList and converts to String, assigns value to emailList.
                val email = etEmail.text.toString() //Gets the text from etEmail and converts to String, assigns value to email.
                if(email.isNotEmpty()){
                    tvEmailList.text = addEmail(emailList, email) //Passes emailList and email to addEmail() than sets tvEmailList.text to the return value.
                    etEmail.text.clear()
                    }
            }
            //Removes email from email list.
            ivRemove.setOnClickListener{
                if(tvEmailList.text.isNotEmpty()){ //If the email list is not empty...
                    val emailList = tvEmailList.text.toString() //Gets the text from tvEmailList and converts to String, assigns value to emailList.
                    tvEmailList.text = removeEmail(emailList) //Passes email list to removeEmail() when called than gives tvEmailList.text the return value.
                }
                //If the email list is anything else...
                else{
                    Toast.makeText(requireActivity(),"No Invitees Listed", Toast.LENGTH_SHORT).show() //Prompt user that the email list is empty with Toast.
                }
            }

            //Prompts user to select a date, once date is picked, formats date and sets to tvDate.text.
            ivDate.setOnClickListener {
                showDatePickerDialog(requireContext()) { year, month, day -> //Calls showDatePickerDialog() and passes context.
                    val selectedDate = "$day/${month + 1}/$year" //Formats the date into dd/MM/yy.
                    tvDate.text = selectedDate //Sets tvDate.text to selectedDate.
                }
            }
            //Prompts user to select a start time, once time is picked, formats time and sets to tvStartTime.text.
            ivStartTime.setOnClickListener {
                showTimePickerDialog(requireContext()) { hour, minute -> //Calls showTimePickerDialog() and passes context.
                    val selectedTime = String.format("%02d:%02d", hour, minute) //Formats the time into hh/MM.
                    tvStartTime.text = selectedTime //Sets tvStartTime.text to selectedTime.
                }
            }

            //Prompts user to select a end time, once time is picked, formats time and sets to tvEndTime.text.
            ivEmdTime.setOnClickListener {
                showTimePickerDialog(requireContext()) { hour, minute -> //Calls showTimePickerDialog() and passes context.
                    val selectedTime = String.format("%02d:%02d", hour, minute) //Formats the time into hh/MM.
                    tvEndTime.text = selectedTime //Sets tvEndTime.text to selectedTime.
                }
            }
        }
    }
    //Removes an email from the email list than returns email list.
    private fun removeEmail(emailList: String): String{
        val numEmails = emailList.lines() //Splits emailList on a new line and assigns to numEmails.
        if(numEmails.isEmpty()){ //If numEmails is empty...
            return emailList //Return emailList as is.
        }
        return numEmails.dropLast(1).joinToString ( "\n" ) //Drops the last element in the list than joins each element on a new line. Returns result.
    }
    //Adds an email to the email list than returns email list.
    private fun addEmail(emailList: String, emailAdd: String): String {
        return if(emailList.isEmpty())
            "$emailList$emailAdd" //Concatenates emailList, new line, and emailAdd. Returns result.
        else
            "$emailList$emailAdd;\n" //Concatenates emailList, new line, and emailAdd. Returns result.
    }
    //Displays a date picker dialog.
    private fun showDatePickerDialog(context: Context, setDate: (year: Int, month: Int, day: Int) -> Unit) {
        val calendar = Calendar.getInstance() //Creates an instance of Calendar.
        val year = calendar.get(Calendar.YEAR) //Get current year from Calendar instance.
        val month = calendar.get(Calendar.MONTH) //Get current month from Calendar instance.
        val day = calendar.get(Calendar.DAY_OF_MONTH) //Get current day from Calendar instance.

        //Creates and displays date picker dialog.
        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDayOfMonth -> //Once date is selected, lambda function is executed.
            setDate(selectedYear, selectedMonth, selectedDayOfMonth)
        }, year, month, day).show()
    }
    //Displays a 24hr time picker dialog.
    private fun showTimePickerDialog(context: Context, setTime: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance() //Creates an instance of Calendar.
        val hour = calendar.get(Calendar.HOUR_OF_DAY) //Get current hour from Calendar instance.
        val minute = calendar.get(Calendar.MINUTE) //Get current minute from Calendar instance.
        //Creates and displays time picker dialog.
        TimePickerDialog(context, { _, selectedHour, selectedMinute -> //Once time is selected, lambda function is executed.
            setTime(selectedHour, selectedMinute)
        }, hour, minute, true).show()
    }
}