package com.nkming.powermenu

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Preference(pref: SharedPreferences, context: Context)
{
	companion object
	{
		@JvmStatic
		fun from(context: Context): Preference
		{
			return Preference(context.getSharedPreferences(context.getString(
					R.string.pref_file), Context.MODE_PRIVATE), context)
		}
	}

	fun commit(): Boolean
	{
		return _editLock.withLock(
		{
			if (_edit.commit())
			{
				__edit = null
				return true
			}
			else
			{
				return false
			}
		})
	}

	fun apply()
	{
		_editLock.withLock(
		{
			_edit.apply()
			__edit = null
		})
	}

	var isOverrideSystemMenu: Boolean
		get()
		{
			return _pref.getBoolean(_overrideSystemMenuKey, false)
		}
		set(v: Boolean)
		{
			_edit.putBoolean(_overrideSystemMenuKey, v)
		}

	private val _overrideSystemMenuKey by lazy(
	{
		_context.getString(R.string.pref_override_system_menu_key)
	})

	private val _context = context
	private val _pref = pref
	private val _edit: SharedPreferences.Editor
		get()
		{
			_editLock.withLock(
			{
				if (__edit == null)
				{
					__edit = _pref.edit()
				}
				return __edit!!
			})
		}
	private var __edit: SharedPreferences.Editor? = null
	private val _editLock = ReentrantLock(true)
}
