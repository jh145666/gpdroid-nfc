// ==================== MODIFIED: Added Default Password Auto-fill and Show/Hide Toggle ====================
/*******************************************************************************
 * Copyright (c) 2014 Michael Hölzl <mihoelzl@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Michael Hölzl <mihoelzl@gmail.com> - initial implementation
 *     Thomas Sigmund - data base, key set, channel set selection and GET DATA integration
 *     Enhanced: Added default password 00000000, show/hide toggle, auto-fill
 ******************************************************************************/
package at.fhooe.usmile.gpjshell;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import at.fhooe.usmile.gpjshell.db.KeysetDataSource;
import at.fhooe.usmile.gpjshell.objects.GPKeyset;

public class AddKeysetActivity extends Activity {
	private EditText editID;
	private EditText editVersion;
	private EditText editName;
	private EditText editMAC;
	private EditText editENC;
	private EditText editKEK;
	private Button mPositive;
	private Button mNegative;
	private Button mBtnFillDefault;
	private CheckBox mCbShowPassword;
	
	// ADDED: Default GP authentication password
	private static final String DEFAULT_KEY = "0000000000000000";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_keyset);
		
		editID = (EditText) findViewById(R.id.edit_keyset_id);
		editVersion = (EditText) findViewById(R.id.edit_keyset_version);
		editName = (EditText) findViewById(R.id.edit_keyset_name);
		editMAC = (EditText) findViewById(R.id.edit_keyset_mac);
		editENC = (EditText) findViewById(R.id.edit_keyset_enc);
		editKEK = (EditText) findViewById(R.id.edit_keyset_kek);
		mPositive = (Button) findViewById(R.id.btn_install_applet);
		mNegative = (Button) findViewById(R.id.btn_list_applets);
		
		// ADDED: Get new UI elements
		mBtnFillDefault = (Button) findViewById(R.id.btn_fill_default);
		mCbShowPassword = (CheckBox) findViewById(R.id.cb_show_password);
		
		// ADDED: Set default values for new keyset
		editID.setText("0");
		editVersion.setText("1");
		editName.setText("GP Default");
		
		// ADDED: Fill default password button
		mBtnFillDefault.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editMAC.setText(DEFAULT_KEY);
				editENC.setText(DEFAULT_KEY);
				editKEK.setText(DEFAULT_KEY);
				Toast.makeText(AddKeysetActivity.this, 
					"已填充默认密码: 00000000", Toast.LENGTH_SHORT).show();
			}
		});
		
		// ADDED: Show/Hide password toggle
		mCbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int inputType;
				if (isChecked) {
					inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
				} else {
					inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
				}
				editMAC.setInputType(inputType);
				editENC.setInputType(inputType);
				editKEK.setInputType(inputType);
			}
		});
		
		mPositive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editID.getText() != null
						&& !editID.getText().toString().equals("")
						&& !editVersion.getText().toString().equals("")) {
					
					//set unique id to -1. it will be set by DB later
					GPKeyset keyset = new GPKeyset(-1, editName.getText()
							.toString(), Integer.valueOf(editID.getText()
							.toString()), Integer.valueOf(editVersion.getText()
							.toString()), editMAC.getText().toString(), editENC
							.getText().toString(), editKEK.getText().toString(), null);
					Intent intent = new Intent();
					intent.putExtra(GPKeyset.KEYSET, keyset);
					setResult(RESULT_OK, intent);
					
					KeysetDataSource source = new KeysetDataSource(AddKeysetActivity.this);
					source.open();
					boolean containsKey = source.containsKeyset(keyset.getName(), getIntent().getExtras().getString("readername"));
					source.close();
					
					if (containsKey)
						createDialog().show();
					else
						finish();
					
				} else {
					Toast.makeText(AddKeysetActivity.this, "Please enter valid ID and Version", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		
		mNegative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
	
	
	public Dialog createDialog() {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.keyset_dialog_title);
        builder.setMessage(R.string.keyset_dialog_ask_overwrite)
               .setPositiveButton(R.string.keyset_positive, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       AddKeysetActivity.this.finish();
                   }
               })
               .setNegativeButton(R.string.keyset_negative, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dialog.cancel();
                   }
               });
        return builder.create();
    }
}
// ==================== END MODIFICATION ====================
