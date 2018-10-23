package com.team9.focus.activities

import android.Manifest
import android.accounts.AccountManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import com.facebook.*
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.team9.focus.R
import com.team9.focus.models.DBHelper
import com.team9.focus.models.objects.Schedule
import com.team9.focus.models.objects.Timeslot
import com.team9.focus.utilities.Utility
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


public class UserProfileActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

	private val mCredential: GoogleAccountCredential by lazy {
		GoogleAccountCredential.usingOAuth2(
				applicationContext, arrayListOf(CalendarScopes.CALENDAR)
		).setBackOff(ExponentialBackOff())
	}
//	private var mCredential : GoogleAccountCredential ? = null

	companion object {
		const private val REQUEST_ACCOUNT_PICKER: Int = 1000
		const private val REQUEST_AUTHORIZATION: Int = 1001
		const private val REQUEST_GOOGLE_PLAY_SERVICES: Int = 1002
		const private val REQUEST_PERMISSION_GET_ACCOUNTS: Int = 1003
		const private val PREF_ACCOUNT_NAME: String = "accountName"
	}

	private var etEmail: EditText? = null
	private var etName: EditText? = null
	private var tvHoursFocused: TextView? = null
	private var btnSaveUserProfile: Button? = null
	private var btnViewBadges: Button? = null
	private var btnViewStats: Button? = null
	private var btnReset: Button? = null
	private var btnGoogleCalendar: Button? = null
	private var btnShareFacebook: Button? = null

	private var btnFacebook: LoginButton? = null
	private var callbackManager: CallbackManager? = null
	private var mShareDialog: ShareDialog? = null

	private var cbOptIn: CheckBox? = null

	private var initialEmail: String? = null
	private var initialName: String? = null
	private var optIn: Boolean = false

	private var mPreferences: SharedPreferences? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_user_profile)

		mPreferences = getSharedPreferences("email", Context.MODE_PRIVATE)
		initialEmail = mPreferences?.getString("email", "")
		initialName = mPreferences?.getString("name", "")
		optIn = mPreferences!!.getBoolean("optIn", false)

		callbackManager = CallbackManager.Factory.create()
		mShareDialog = ShareDialog(this)

		initLayout()
		addListeners()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		Log.d("REQUEST CODE", "$requestCode")
		Log.d("RES CODE", "$resultCode")

//		callbackManager!!.onActivityResult(requestCode, resultCode, intent)
		if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()
				&& resultCode == RESULT_OK) {
			Log.d("FB Manager", "Request code is ok")
			callbackManager!!.onActivityResult(requestCode, resultCode, data)
		}
		when (requestCode) {
			REQUEST_GOOGLE_PLAY_SERVICES -> {
				if (resultCode != RESULT_OK) {
					Toast.makeText(this, "This app requires Google Play Services.",
							Toast.LENGTH_LONG).show()
				} else {
					getResultsFromApi()
				}
			}
			REQUEST_ACCOUNT_PICKER -> {
				if (resultCode == RESULT_OK && data != null && data.extras != null) {
					val accountName: String = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
					if (accountName != null) {
						val prefs: SharedPreferences = getPreferences(Context.MODE_PRIVATE)
						val editor: SharedPreferences.Editor = prefs.edit()
						editor.putString(PREF_ACCOUNT_NAME, accountName)
						editor.apply()
						mCredential.setSelectedAccountName(accountName)
						getResultsFromApi()
					}
				}
			}
			REQUEST_AUTHORIZATION -> {
				if (resultCode == RESULT_OK) {
					getResultsFromApi()
				}
			}

			else -> {
				Log.d("UPA", "Else statement entered")
			}
		}
	}

	override fun onResume() {
		super.onResume()
		this.initialEmail = mPreferences?.getString("email", "")
		this.initialName = mPreferences?.getString("name", "")
		this.optIn = mPreferences!!.getBoolean("optIn", false)
		cbOptIn?.isChecked = this.optIn
		etEmail?.setText(this.initialEmail)
		etName?.setText(this.initialName)
	}

	private fun initLayout() {
		etEmail = findViewById(R.id.etEmail)
		etName = findViewById(R.id.etName)
		tvHoursFocused = findViewById(R.id.tvHoursFocused)
		btnSaveUserProfile = findViewById(R.id.btnSaveUserProfile)
		btnSaveUserProfile?.isEnabled = false
		btnViewStats = findViewById(R.id.btnStats)
		btnViewBadges = findViewById(R.id.btnBadges)
		btnReset = findViewById(R.id.btnReset)
		btnGoogleCalendar = findViewById(R.id.btnGoogleCalendar)
		cbOptIn = findViewById(R.id.cbOptIn)
		cbOptIn?.isChecked = this.optIn
		btnShareFacebook = findViewById(R.id.btnShareFacebook)
		btnFacebook = findViewById(R.id.btnFacebook)
		btnFacebook!!.setReadPermissions("email, publish_actions")
		btnFacebook!!.registerCallback(callbackManager!!, object: FacebookCallback<LoginResult> {
			override fun onSuccess(result: LoginResult) {
				println("=========================onsuccess=========================")
				val accessToken = AccessToken.getCurrentAccessToken()
				val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
					println("===================JSON++" + `object`)

					var SfacebookID = ""
					var Sname = ""
					var Semail = ""
					var Sgender = ""
					var Surl = ""
					val Sphone = ""

					try {

						if (`object`.has("id")) {
							SfacebookID = `object`.getString("id")
						}

						if (`object`.has("name")) {
							Sname = `object`.getString("name")
						}

						if (`object`.has("email")) {
							Semail = `object`.getString("email")
						}

						if (`object`.has("gender")) {
							Sgender = `object`.getString("gender")
						}

						if (`object`.has("picture")) {
							Surl = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
						}


					} catch (e: Exception) {
						e.printStackTrace()
					}
				}
				val parameters = Bundle()
				parameters.putString("fields", "id,name,link,email,picture,gender, birthday")
				request.parameters = parameters
				request.executeAsync()

			}

			override fun onCancel() {

				println("=========================onCancel=========================")
				Toast.makeText(this@UserProfileActivity, "Cancel", Toast.LENGTH_LONG).show()
			}

			override fun onError(error: FacebookException) {
				error.printStackTrace()
				println("=========================onError=========================")
				println(error.toString())
				Toast.makeText(this@UserProfileActivity, "onError", Toast.LENGTH_LONG).show()
			}

		})
	}

	val mTextWatcher = object : TextWatcher {
		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		override fun afterTextChanged(s: Editable?) {
			if (etEmail?.text.toString() != initialEmail
					|| etName?.text.toString() != initialName) {
				btnSaveUserProfile?.isEnabled = true
			}
		}
	}

	private fun addListeners() {
		etEmail?.addTextChangedListener(mTextWatcher)
		etName?.addTextChangedListener(mTextWatcher)

		btnSaveUserProfile?.setOnClickListener {
			val editor = mPreferences!!.edit()
			editor.putString("email", etEmail!!.text.toString())
			editor.putString("name", etName!!.text.toString())
			editor.putBoolean("optIn", cbOptIn!!.isChecked)
			editor.commit()
			Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show()
		}

		btnViewBadges?.setOnClickListener {
			val intent = Intent(this, ViewBadgeActivity::class.java)
			startActivity(intent)
		}

		btnViewStats?.setOnClickListener {
			val intent = Intent(this, ViewStatActivity::class.java)
			startActivity(intent)
		}

		btnReset?.setOnClickListener {
        	 Utility.sendEmail(this);
        	 DBHelper.resetAppCount();
        }

		btnGoogleCalendar?.setOnClickListener {
			btnGoogleCalendar?.isEnabled = false
			getResultsFromApi()
			btnGoogleCalendar?.isEnabled = true
		}

		btnShareFacebook?.setOnClickListener {
			if (ShareDialog.canShow(ShareLinkContent::class.java)) {
				val content = ShareLinkContent.Builder()
						.setContentUrl(Uri.parse("https://www.team9focusandroid.com"))
						.setContentTitle("Focus!")
						.setContentDescription("Focus is an application that helps users stay on task")
						.build()
				mShareDialog?.show(content)
			}
		}

	}

	// Functions below are for Google Calendar OAuth
	private fun getResultsFromApi() {
		if (!isGooglePlayServicesAvailable()) {
			acquireGooglePlayServices()
		} else if (mCredential.selectedAccountName == null) {
			chooseAccount()
		} else if (!isDeviceOnline()) {
			Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show()
		} else {
			val events = MakeRequestTask(mCredential).execute()
			Toast.makeText(this@UserProfileActivity, "Synced with Google Calendar", Toast.LENGTH_LONG).show()
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
	}

	private fun isGooglePlayServicesAvailable(): Boolean {
		val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
		val connectionStatusCode: Int = apiAvailability.isGooglePlayServicesAvailable(this)
		return connectionStatusCode == ConnectionResult.SUCCESS
	}

	private fun acquireGooglePlayServices() {
		val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
		val connectionStatusCode: Int = apiAvailability.isGooglePlayServicesAvailable(this)
		if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
			val dialog: Dialog = apiAvailability.getErrorDialog(
					this@UserProfileActivity,
					connectionStatusCode,
					REQUEST_GOOGLE_PLAY_SERVICES
			)
			dialog.show()
		}
	}

	@AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
	private fun chooseAccount() {
		if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
			val accountName: String? = getPreferences(Context.MODE_PRIVATE)
					.getString(PREF_ACCOUNT_NAME, null)
			if (accountName != null) {
				mCredential.setSelectedAccountName(accountName)
				getResultsFromApi()
			} else {
				startActivityForResult(
						mCredential.newChooseAccountIntent(),
						REQUEST_ACCOUNT_PICKER
				)
			}
		} else {
			EasyPermissions.requestPermissions(
					this,
					"This app needs to access your Google account",
					REQUEST_PERMISSION_GET_ACCOUNTS,
					Manifest.permission.GET_ACCOUNTS
			)
		}
	}

	private fun isDeviceOnline(): Boolean {
		val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkInfo: NetworkInfo = connManager.activeNetworkInfo
		return (networkInfo != null && networkInfo.isConnected)
	}

	override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
		Log.d("UPA", "Permissions Denied")
	}

	override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
		Log.d("UPA", "Permissions Granted")
	}

	// Facilitates creating google calendar events and flattening timeslots
	data class TimeslotEvent(val dayOfWeek: Int, var endHour: Int, var endMinute: Int,
							 var startHour: Int, var startMinute: Int)

	// Private class for async task
	private inner class MakeRequestTask : AsyncTask<Void, Void, List<String>> {

		private var mService: Calendar? = null
		private var mLastError: Exception? = null

		constructor(credential: GoogleAccountCredential) {
			val transport: HttpTransport = AndroidHttp.newCompatibleTransport()
			val jsonFactory = JacksonFactory.getDefaultInstance()
			mService = Calendar.Builder(transport, jsonFactory, credential)
					.setApplicationName("Focus!")
					.build()
		}

		override fun doInBackground(vararg params: Void?): List<String>? {
			Log.d("MRT", "Do In Background Entered")
			try {
				return createEvents()!!.map { it.summary }
			} catch (e: Exception) {
				Log.d("MRT", "Exception Caught: ${e.message}")
				mLastError = e
				cancel(true)
				return null
			}
		}

		private fun createEvents(): List<Event>? {
			val events = ArrayList<Event>()
			val schedules : List<Schedule> = DBHelper.getAllSchedules()
			for (schedule in schedules) {

				val timeslots : List<Timeslot> = DBHelper.getTimeslotsBySchedule(schedule)
				var appNameSet = HashSet<String>()
				for (profile in DBHelper.getProfilesBySchedule(schedule)) {
					appNameSet.addAll(DBHelper.getAppsByProfile(profile).map { it.appName })
				}

				val description : String = "Blocked Apps: " + appNameSet.mapIndexed { index, s ->
					if (index == appNameSet.size-1) s else "$s, "
				}


				val timeslotEvents = ArrayList<TimeslotEvent>()
				for (timeslot in timeslots) {
					for (day in timeslot.dayOfWeek.split(",").filter { it != "" }) {
						timeslotEvents.add(TimeslotEvent(day.toInt(), timeslot.endHour, timeslot.endMinute,
								timeslot.startHour, timeslot.startMinute))
					}
				}
				for (timeslot in timeslotEvents) {
					var event = Event()
							.setSummary(schedule.scheduleName)
							.setDescription(description)

					val cal = java.util.Calendar.getInstance()
					val month = cal.get(java.util.Calendar.MONTH)
					val year = cal.get(java.util.Calendar.YEAR)
					cal.set(java.util.Calendar.HOUR_OF_DAY, 0)

					while (cal.get(java.util.Calendar.DAY_OF_WEEK) != timeslot.dayOfWeek) {
						cal.add(java.util.Calendar.DATE, 1)
					}
					cal.set(java.util.Calendar.HOUR_OF_DAY, timeslot.startHour)
					cal.set(java.util.Calendar.MINUTE, timeslot.startMinute)
					val startDateTime : DateTime = DateTime(cal.time)
					val startEvent : EventDateTime = EventDateTime()
							.setDateTime(startDateTime)
							.setTimeZone("America/Los_Angeles")

					event.setStart(startEvent)
					cal.set(java.util.Calendar.HOUR_OF_DAY, timeslot.endHour)
					cal.set(java.util.Calendar.MINUTE, timeslot.endMinute)
					val endDateTime : DateTime = DateTime(cal.time)
					val endEvent : EventDateTime = EventDateTime()
							.setDateTime(endDateTime)
							.setTimeZone("America/Los_Angeles")
					event.setEnd(endEvent)

					var dayOfWeekRRule = when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
						1 -> "SU"
						2 -> "MO"
						3 -> "TU"
						4 -> "WE"
						5 -> "TH"
						6 -> "FR"
						7 -> "SA"
						else -> "SU"
					}
					// recur weekly
					val recurrence = arrayListOf<String>("RRULE:FREQ=WEEKLY;BYDAY=$dayOfWeekRRule;INTERVAL=1")
					event.setRecurrence(recurrence)
					// set the attendees
					val attendees = arrayListOf<EventAttendee>(
							EventAttendee().setEmail(mCredential.selectedAccountName)
					)
					event.setAttendees(attendees)

					val reminders = Event.Reminders().setUseDefault(true)
					event.setReminders(reminders)

					event = mService!!
							.events()
							.insert("primary", event)
							.execute()
					events.add(event)
				}
			}
			return events
		}



		override fun onPreExecute() {
			super.onPreExecute()
			Log.d("MRT", "Pre Execute")
		}

		override fun onPostExecute(result: List<String>?) {
			super.onPostExecute(result)
			Log.d("MRT", "Post Execute")
		}

		override fun onCancelled() {
			super.onCancelled()
			if (mLastError != null) {
				if (mLastError is GooglePlayServicesAvailabilityIOException) {
					val apiAvailability = GoogleApiAvailability.getInstance()
					val connectionCode = (mLastError as GooglePlayServicesAvailabilityIOException)
							.connectionStatusCode
					val dialog: Dialog = apiAvailability.getErrorDialog(
							this@UserProfileActivity,
							connectionCode,
							REQUEST_GOOGLE_PLAY_SERVICES
					)
					dialog.show()
				} else if (mLastError is UserRecoverableAuthIOException) {
					startActivityForResult(
							(mLastError as UserRecoverableAuthIOException).intent,
							REQUEST_AUTHORIZATION
					)
					Log.d("MRT", "Authorization granted")
				} else {
					Toast.makeText(
							this@UserProfileActivity,
							(mLastError as java.lang.Exception).message,
							Toast.LENGTH_LONG
					).show()
				}
			} else {
				Toast.makeText(
						this@UserProfileActivity,
						"Request Cancelled",
						Toast.LENGTH_LONG
				).show()
			}
		}
	}
}

