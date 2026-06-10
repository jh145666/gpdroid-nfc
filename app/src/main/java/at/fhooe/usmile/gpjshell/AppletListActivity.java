// ==================== MODIFIED: Enhanced Applet Management with Batch Operations ====================
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
 *     Enhanced: Added delete buttons, batch operations, select all/deselect all
 ******************************************************************************/
package at.fhooe.usmile.gpjshell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.gpj.cardservices.AID;
import net.sourceforge.gpj.cardservices.AIDRegistryEntry;
import net.sourceforge.gpj.cardservices.GPUtil;
import net.sourceforge.gpj.cardservices.interfaces.GPTerminal;
import net.sourceforge.gpj.cardservices.interfaces.NfcTerminal;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import at.fhooe.usmile.gpjshell.MainActivity.APDU_COMMAND;
import at.fhooe.usmile.gpjshell.objects.GPChannelSet;
import at.fhooe.usmile.gpjshell.objects.GPKeyset;

public class AppletListActivity extends Activity implements AppletDetailActivity.NoticeAppletEventListener {
	private static final String LOG_TAG = "AppletListActivity";
	public static final String EXTRA_CHANNELSET = "extra_channelset";
	public static final String EXTRA_KEYSET = "extra_keyset";
	public static final String EXTRA_SEEKREADER = "extra_reader";
	
	private AppletListAdapter mListAdapter;
	private GPKeyset mKeySet;
	private GPChannelSet mChannelSet;
	private int mSeekReader;
	private List<AIDRegistryEntry> mRegistry;
	private Set<Integer> mSelectedPositions;
	private boolean mBatchMode = false;
	
	private Button mBtnSelectAll, mBtnDeselectAll, mBtnBatchDelete, mBtnToggleBatch;
	private TextView mTvStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ADDED: Use enhanced layout with batch operation buttons
		setContentView(R.layout.applet_list_enhanced);
		
		mSelectedPositions = new HashSet<>();
		
		final ListView listview = (ListView) findViewById(R.id.listview);
		mBtnSelectAll = (Button) findViewById(R.id.btn_select_all);
		mBtnDeselectAll = (Button) findViewById(R.id.btn_deselect_all);
		mBtnBatchDelete = (Button) findViewById(R.id.btn_batch_delete);
		mBtnToggleBatch = (Button) findViewById(R.id.btn_toggle_batch);
		mTvStatus = (TextView) findViewById(R.id.tv_status);
		
		setListData(listview);
		
		mKeySet = (GPKeyset) getIntent().getSerializableExtra(EXTRA_KEYSET);
		mChannelSet = (GPChannelSet) getIntent().getSerializableExtra(EXTRA_CHANNELSET);
		mSeekReader = (Integer) getIntent().getSerializableExtra(EXTRA_SEEKREADER);
		
		// ADDED: Toggle batch mode
		mBtnToggleBatch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleBatchMode();
			}
		});
		
		// ADDED: Select all
		mBtnSelectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < mRegistry.size(); i++) {
					if (!mRegistry.get(i).isSecurityDomain()) {
						mSelectedPositions.add(i);
					}
				}
				mListAdapter.notifyDataSetChanged();
				updateStatus();
			}
		});
		
		// ADDED: Deselect all
		mBtnDeselectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectedPositions.clear();
				mListAdapter.notifyDataSetChanged();
				updateStatus();
			}
		});
		
		// ADDED: Batch delete
		mBtnBatchDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedPositions.isEmpty()) {
					new AlertDialog.Builder(AppletListActivity.this)
						.setTitle("提示")
						.setMessage("请先选择要删除的Applet")
						.setPositiveButton("确定", null)
						.show();
					return;
				}
				
				new AlertDialog.Builder(AppletListActivity.this)
					.setTitle("确认批量删除")
					.setMessage("确定要删除选中的 " + mSelectedPositions.size() + " 个Applet吗？\n此操作不可恢复！")
					.setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							batchDeleteSelected();
						}
					})
					.setNegativeButton("取消", null)
					.show();
			}
		});
	}
	
	// ADDED: Toggle batch mode
	private void toggleBatchMode() {
		mBatchMode = !mBatchMode;
		mSelectedPositions.clear();
		
		int visibility = mBatchMode ? View.VISIBLE : View.GONE;
		mBtnSelectAll.setVisibility(visibility);
		mBtnDeselectAll.setVisibility(visibility);
		mBtnBatchDelete.setVisibility(visibility);
		
		mBtnToggleBatch.setText(mBatchMode ? "退出批量" : "批量模式");
		
		if (mBatchMode) {
			mTvStatus.setText("批量模式：勾选要删除的Applet");
		} else {
			mTvStatus.setText("点击Applet查看详情，点击删除按钮直接删除");
		}
		
		mListAdapter.notifyDataSetChanged();
	}
	
	// ADDED: Update status text
	private void updateStatus() {
		if (mBatchMode) {
			mTvStatus.setText("已选择 " + mSelectedPositions.size() + " 个Applet");
		}
	}
	
	// ADDED: Batch delete
	private void batchDeleteSelected() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					List<Integer> positions = new ArrayList<>(mSelectedPositions);
					for (int i = positions.size() - 1; i >= 0; i--) {
						int pos = positions.get(i);
						AIDRegistryEntry entry = mRegistry.get(pos);
						if (!entry.isSecurityDomain()) {
							GPConnection.getInstance(getApplicationContext()).setSelectedApplet(pos);
							GPCommand cmd = new GPCommand(
								APDU_COMMAND.APDU_DELETE_SELECTED_APPLET, 
								mSeekReader, null, (byte)0, null);
							GPTerminal term = NfcTerminal.getInstance(getApplicationContext());
							GPConnection.getInstance(getApplicationContext()).performCommand(term, mKeySet, mChannelSet, cmd);
						}
					}
					return true;
				} catch (Exception e) {
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean success) {
				mRegistry = GPConnection.getInstance(AppletListActivity.this).getRegistry();
				mSelectedPositions.clear();
				mListAdapter.updateData(mRegistry);
				toggleBatchMode();
				
				new AlertDialog.Builder(AppletListActivity.this)
					.setTitle(success ? "成功" : "失败")
					.setMessage(success ? "批量删除完成" : "批量删除过程中出现错误")
					.setPositiveButton("确定", null)
					.show();
			}
		}.execute();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void returnToHome(){
		Intent homeIntent= new Intent(this, MainActivity.class);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
	}
	
	private void setListData(final ListView listview) {
		List<AIDRegistryEntry> registry = GPConnection.getInstance(getApplicationContext()).getRegistry();
		
		if(registry == null){
			returnToHome();
		} else {
			mRegistry = registry;
			mListAdapter = new AppletListAdapter(this, registry);
			listview.setAdapter(mListAdapter);
		}
	}
	
	public void showAppletDetailsDialog(boolean isSd, int position) {
		GPConnection.getInstance(getApplicationContext()).setSelectedApplet(position);
        DialogFragment dialog = new AppletDetailActivity();
        dialog.show(getFragmentManager(), "AppletDetailFragment");
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSecurityDomain", isSd);
        dialog.setArguments(bundle);
    }
	
	// ADDED: Single delete with confirmation
	private void deleteSingleApplet(final int position) {
		final AIDRegistryEntry entry = mRegistry.get(position);
		if (entry.isSecurityDomain()) {
			new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("安全域不能删除")
				.setPositiveButton("确定", null)
				.show();
			return;
		}
		
		new AlertDialog.Builder(this)
			.setTitle("确认删除")
			.setMessage("确定要删除此Applet吗？\nAID: " + entry.getAID().toString())
			.setPositiveButton("删除", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					GPConnection.getInstance(getApplicationContext()).setSelectedApplet(position);
					GPCommand cmd = new GPCommand(
						APDU_COMMAND.APDU_DELETE_SELECTED_APPLET, 
						mSeekReader, null, (byte)0, null);
					GPTerminal term = NfcTerminal.getInstance(getApplicationContext());
					GPConnection.getInstance(getApplicationContext()).performCommand(term, mKeySet, mChannelSet, cmd);
					
					MainActivity.log().log(LOG_TAG, "Successfully removed: " + entry.getAID());
					
					mRegistry = GPConnection.getInstance(AppletListActivity.this).getRegistry();
					mListAdapter.updateData(mRegistry);
				}
			})
			.setNegativeButton("取消", null)
			.show();
	}

	@Override
	public void onDialogDeleteClick(DialogFragment dialog) {
		AID delAID = GPConnection.getInstance(getApplicationContext()).getSelectedApplet().getAID();
		GPCommand cmd = new GPCommand(APDU_COMMAND.APDU_DELETE_SELECTED_APPLET, mSeekReader, null, (byte)0, null);
		GPTerminal term = NfcTerminal.getInstance(getApplicationContext());
		GPConnection.getInstance(getApplicationContext()).performCommand(term, mKeySet, mChannelSet, cmd);
	
		MainActivity.log().log(LOG_TAG, "Successfully removed: "+delAID);
		
		mRegistry = GPConnection.getInstance(this).getRegistry();
		mListAdapter.updateData(mRegistry);
	}

	@Override
	public void onDialogOkClick(DialogFragment dialog) {
		
	}
	
	// ADDED: Custom Adapter with delete button and checkbox
	class AppletListAdapter extends BaseAdapter {
		private Context mContext;
		private List<AIDRegistryEntry> mData;
		private LayoutInflater mInflater;
		
		public AppletListAdapter(Context context, List<AIDRegistryEntry> data) {
			mContext = context;
			mData = data;
			mInflater = LayoutInflater.from(context);
		}
		
		public void updateData(List<AIDRegistryEntry> data) {
			mData = data;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return mData.size();
		}
		
		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.applet_list_item, parent, false);
				holder = new ViewHolder();
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_select);
				holder.tvAidHex = (TextView) convertView.findViewById(R.id.tv_aid_hex);
				holder.tvAidReadable = (TextView) convertView.findViewById(R.id.tv_aid_readable);
				holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_applet_info);
				holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btn_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final AIDRegistryEntry entry = mData.get(position);
			String aidHex = GPUtil.byteArrayToString(entry.getAID().getBytes());
			String aidReadable = GPUtil.byteArrayToReadableString(entry.getAID().getBytes());
			
			holder.tvAidHex.setText(aidHex);
			holder.tvAidReadable.setText(aidReadable);
			holder.tvInfo.setText(String.format("Kind: %s | LifeCycle: %d | Priv: 0x%02X",
				entry.getKind().toShortString(),
				entry.getLifeCycleState(),
				entry.getPrivileges()));
			
			// Batch mode checkbox
			holder.checkBox.setVisibility(mBatchMode ? View.VISIBLE : View.GONE);
			holder.checkBox.setChecked(mSelectedPositions.contains(position));
			
			// Disable delete for Security Domain
			if (entry.isSecurityDomain()) {
				holder.btnDelete.setVisibility(View.GONE);
				holder.checkBox.setEnabled(false);
				holder.tvAidHex.setTextColor(0xFF888888);
			} else {
				holder.btnDelete.setVisibility(mBatchMode ? View.GONE : View.VISIBLE);
				holder.checkBox.setEnabled(true);
				holder.tvAidHex.setTextColor(0xFF000000);
			}
			
			// Checkbox toggle
			holder.checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSelectedPositions.contains(position)) {
						mSelectedPositions.remove(position);
					} else {
						mSelectedPositions.add(position);
					}
					updateStatus();
				}
			});
			
			// Delete button
			holder.btnDelete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteSingleApplet(position);
				}
			});
			
			// Item click - show details
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mBatchMode) {
						if (!entry.isSecurityDomain()) {
							if (mSelectedPositions.contains(position)) {
								mSelectedPositions.remove(position);
							} else {
								mSelectedPositions.add(position);
							}
							notifyDataSetChanged();
							updateStatus();
						}
					} else {
						showAppletDetailsDialog(entry.isSecurityDomain(), position);
					}
				}
			});
			
			return convertView;
		}
		
		class ViewHolder {
			CheckBox checkBox;
			TextView tvAidHex;
			TextView tvAidReadable;
			TextView tvInfo;
			ImageButton btnDelete;
		}
	}
}
// ==================== END MODIFICATION ====================
