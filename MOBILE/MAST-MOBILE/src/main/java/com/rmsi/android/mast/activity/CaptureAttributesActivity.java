package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.adapter.SpinnerAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

public class CaptureAttributesActivity extends ActionBarActivity {
    private ImageView personInfo, tenureInfo, multimedia, custom, propertyInfo;
    private List<Attribute> attrList;
    private final Context context = this;
    private CommonFunctions cf = CommonFunctions.getInstance();
    private int groupId = 0;
    private Long featureId = 0L, witnessId_1 = 0L, witnessId_2;
    private static String serverFeatureId = null;
    private String personType = "Select an option", hamletName_Id, witness_1, witness_2;
    private boolean flag = false;
    private DBController db = new DBController(context);
    private Spinner spinnerPersonType, spinnerHamlet, spinnerWitness1, spinnerWitness2;
    private Option selecteditem;
    private String msg = "";
    private String infoSingleOccupantStr, infoMultipleJointStr, infoMultipleTeneancyStr, infoTenancyInProbateStr, infoGuardianMinorStr, infoStr;
    private String You_have_selected, yesStr, noStr;
    private int roleId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunctions.getInstance().Initialize(getApplicationContext());
        cf.loadLocale(getApplicationContext());

        setContentView(R.layout.activity_capture_attributes);
        roleId = CommonFunctions.getRoleID();

        TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);
        TextView VillageName = (TextView) findViewById(R.id.villageName_lbl);
        spinnerPersonType = (Spinner) findViewById(R.id.spinner_person_type);
        List<Option> hamletList = new ArrayList<Option>();
        hamletList = db.getHamletOptions();
        List<Option> witnessList = new ArrayList<Option>();
        witnessList = db.getAdjudicators();
        spinnerHamlet = (Spinner) findViewById(R.id.spinner_hemlet);
        spinnerWitness1 = (Spinner) findViewById(R.id.spinner_witness1);
        spinnerWitness2 = (Spinner) findViewById(R.id.spinner_witness2);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this.context, android.R.layout.simple_spinner_item, hamletList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHamlet.setAdapter(spinnerAdapter);

        SpinnerAdapter spinnerAdapterWitness1 = new SpinnerAdapter(this.context, android.R.layout.simple_spinner_item, witnessList);
        spinnerAdapterWitness1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWitness1.setAdapter(spinnerAdapterWitness1);

        SpinnerAdapter spinnerAdapterWitness2 = new SpinnerAdapter(this.context, android.R.layout.simple_spinner_item, witnessList);
        spinnerAdapterWitness2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWitness2.setAdapter(spinnerAdapterWitness2);

        infoStr = getResources().getString(R.string.info);
        infoSingleOccupantStr = getResources().getString(R.string.infoSingleOccupantStr);
        infoMultipleJointStr = getResources().getString(R.string.infoMultipleJointStr);
        infoMultipleTeneancyStr = getResources().getString(R.string.infoMultipleTeneancyStr);
        infoTenancyInProbateStr = getResources().getString(R.string.infoTenancyInProbateStr);
        infoGuardianMinorStr = getResources().getString(R.string.infoGuardianMinorStr);
        You_have_selected = getResources().getString(R.string.You_have_selected);
        yesStr = getResources().getString(R.string.yes);
        noStr = getResources().getString(R.string.no);

        if (roleId == 2)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
        {
            spinnerPersonType.setEnabled(false);
            spinnerHamlet.setEnabled(false);
            spinnerWitness1.setEnabled(false);
            spinnerWitness2.setEnabled(false);
        }

        spinnerPersonType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                if (pos == 1)
                    personType = "Natural";
                else if (pos == 2)
                    personType = "Non-Natural";
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                personType = "Select an option";
            }
        });

        spinnerHamlet.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                selecteditem = (Option) spinnerHamlet.getSelectedItem();
                hamletName_Id = selecteditem.getOptionId().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinnerWitness1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selecteditem = (Option) spinnerWitness1.getSelectedItem();
                witness_1 = selecteditem.getOptionName();
                witnessId_1 = selecteditem.getOptionId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinnerWitness2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                selecteditem = (Option) spinnerWitness2.getSelectedItem();
                witnessId_2 = selecteditem.getOptionId();
                witness_2 = selecteditem.getOptionName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
            serverFeatureId = extras.getString("Server_featureid");
        }

        flag = db.isGeneralAttributeSaved(featureId);
        if (flag) {
            spinnerPersonType.setEnabled(false);
        }

        VillageName.setText(VillageName.getText() + " : " + db.villageName());

        if (!TextUtils.isEmpty(serverFeatureId) && serverFeatureId != null) {
            spatialunitValue.setText("USIN" + " : " + serverFeatureId.toString());
        } else {
            spatialunitValue.setText(spatialunitValue.getText() + "   :  " + featureId.toString());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_capture_attributes);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (featureId == 0) {
            attrList = db.getGeneralAttribute(cf.getLocale());
        } else {
            String keyword = "general";
            attrList = db.getFeatureGenaralInfo(featureId, keyword, cf.getLocale());
            //attrList = db.getGneralAttributeData(featureId, keyword, cf.getLocale());
            String personType = attrList.get(0).getPersonType();
            String witness1 = attrList.get(0).getWitness1();
            String witness2 = attrList.get(0).getWitness2();
            int hamletId = attrList.get(0).getHamletId();
            for (int i = 0; i < hamletList.size(); i++) {
                if (hamletList.get(i).getOptionId() == hamletId) {
                    spinnerHamlet.setSelection(i);
                }
            }

            for (int i = 0; i < witnessList.size(); i++) {
                if (witnessList.get(i).getOptionName().equalsIgnoreCase(witness1)) {
                    spinnerWitness1.setSelection(i);
                }
                if (witnessList.get(i).getOptionName().equalsIgnoreCase(witness2)) {
                    spinnerWitness2.setSelection(i);
                }
            }

            if (!TextUtils.isEmpty(personType) && personType.equalsIgnoreCase("Natural")) {
                spinnerPersonType.setSelection(1);
            } else if (!TextUtils.isEmpty(personType) && personType.equalsIgnoreCase("Non-Natural")) {
                spinnerPersonType.setSelection(2);
            }

            if (attrList.size() > 0) {
                groupId = attrList.get(0).getGroupId();
            }
        }

        db.close();

        LinearLayout layoutAttributes = (LinearLayout) findViewById(R.id.layoutAttributes);
        GuiUtility.appendLayoutWithAttributes(layoutAttributes, attrList);

        personInfo = (ImageView) findViewById(R.id.btn_personlist);
        propertyInfo = (ImageView) findViewById(R.id.btn_propertyInfo);
        tenureInfo = (ImageView) findViewById(R.id.btn_tenureInfo);
        multimedia = (ImageView) findViewById(R.id.btn_addMultimedia);
        custom = (ImageView) findViewById(R.id.btn_addcustom);

        //For tooltip text
        View viewForTenureToolTip = tenureInfo;
        View viewForPersonToolTip = personInfo;
        View viewForMediaToolTip = multimedia;
        View viewForCustomToolTip = custom;
        View viewForPropertyDetailsToolTip = propertyInfo;

        String add_person = getResources().getString(R.string.AddPerson);
        String add_social_tenure = getResources().getString(R.string.AddSocialTenureInfo);
        String add_multimedia = getResources().getString(R.string.AddNewMultimedia);
        String add_custom_attrib = getResources().getString(R.string.add_custom_attributes);
        String add_property_details = getResources().getString(R.string.AddNewPropertyDetails);

        cf.setup(viewForPersonToolTip, add_person);
        cf.setup(viewForTenureToolTip, add_social_tenure);
        cf.setup(viewForMediaToolTip, add_multimedia);
        cf.setup(viewForCustomToolTip, add_custom_attrib);
        cf.setup(viewForPropertyDetailsToolTip, add_property_details);

        personInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (roleId == 1) {

                    boolean tenureFilled = db.IsTenureValue(featureId);
                    if (spinnerPersonType.getSelectedItemPosition() == 1) {
                        //flag=db.getFormValues(featureId);
                        flag = db.isGeneralAttributeSaved(featureId);

                        if (flag) {
                            if (tenureFilled) {
                                Option tenureType = db.getTenureTypeOptionsValue(featureId);
                                final long tenureTypeId = tenureType.getOptionId();
                                if (tenureTypeId == 70 || tenureTypeId == 71 || tenureTypeId == 72 || tenureTypeId == 100 || tenureTypeId == 99) {

                                    String infoMsg = "No msg";
                                    //info message
                                    switch ((int) tenureTypeId) {
                                        case 70:
                                            //cf.showMessage(context,"Info","You can add only one adult owner & multiple person of interests");
                                            //infoMsg=infoSingleOccupantStr;
                                            infoMsg = infoMultipleTeneancyStr; //for live

                                            break;
                                        case 71:
                                            //cf.showMessage(context,"Info","You can add two adult owners & multiple person of interests");
                                            //infoMsg=infoMultipleJointStr;
                                            infoMsg = infoSingleOccupantStr; //for live
                                            break;
                                        case 72:
                                            //cf.showMessage(context,"Info","You can add two or more adult owners & multiple person of interests");
                                            //infoMsg=infoMultipleTeneancyStr;
                                            infoMsg = infoMultipleJointStr; //for live
                                            break;

                                        case 99:
                                            //cf.showMessage(context,"Info","You can add multiple minor owners & two guardian");
                                            infoMsg = infoTenancyInProbateStr;
                                            break;

                                        case 100:
                                            //cf.showMessage(context,"Info","You can add multiple minor owners & two guardian");
                                            infoMsg = infoGuardianMinorStr;
                                            break;

                                        default:
                                            break;
                                    }

                                    final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                                    dialog.setContentView(R.layout.dialog_for_info);
                                    dialog.setTitle(getResources().getString(R.string.info));
                                    dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
                                    Button proceed = (Button) dialog.findViewById(R.id.btn_proceed);
                                    Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                                    final TextView txtTenureType = (TextView) dialog.findViewById(R.id.textView_tenure_type);
                                    final TextView txtInfoMsg = (TextView) dialog.findViewById(R.id.textView_infoMsg);
                                    final TextView cnfrmMsg = (TextView) dialog.findViewById(R.id.textView_cnfrm_msg);
                                    cnfrmMsg.setVisibility(View.VISIBLE);
                                    txtTenureType.setText(You_have_selected + " " + tenureType.getOptionName());
                                    txtInfoMsg.setText(infoMsg);
                                    proceed.setText(yesStr);
                                    cancel.setText(noStr);

                                    proceed.setOnClickListener(new OnClickListener() {
                                        //Run when button is clicked
                                        @Override
                                        public void onClick(View v) {
                                            Intent myIntent;
                                            if (tenureTypeId == 99) {
                                                myIntent = new Intent(context, PersonListWithDPActivity.class);
                                            } else {
                                                myIntent = new Intent(context, PersonListActivity.class);
                                            }

                                            myIntent.putExtra("featureid", featureId);
                                            myIntent.putExtra("persontype", "natural");
                                            myIntent.putExtra("serverFeaterID", serverFeatureId);
                                            startActivity(myIntent);
                                            dialog.dismiss();

                                        }
                                    });

                                    cancel.setOnClickListener(new OnClickListener() {
                                        //Run when button is clicked
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                            } else {
                                String msg = getResources().getString(string.fill_tenure);
                                String warning = getResources().getString(string.warning);
                                cf.showMessage(context, warning, msg);
                            }
                        } else {
                            String msg = getResources().getString(string.save_genral_attrribute);
                            String warning = getResources().getString(string.warning);
                            cf.showMessage(context, warning, msg);
                        }

                    } else if (spinnerPersonType.getSelectedItemPosition() == 2) {
                        //flag=db.getFormValues(featureId);
                        flag = db.isGeneralAttributeSaved(featureId);
                        if (flag) {
                            Intent myIntent = new Intent(context, AddNonNaturalPersonActivity.class);
                            myIntent.putExtra("featureid", featureId);
                            startActivity(myIntent);
                        } else {
                            String msg = getResources().getString(string.save_genral_attrribute);
                            String warning = getResources().getString(string.warning);
                            cf.showMessage(context, warning, msg);
                        }
                    }
                } else if (roleId == 2) {
                    if (spinnerPersonType.getSelectedItemPosition() == 1) {
                        Option tenureType = db.getTenureTypeOptionsValue(featureId);
                        final long tenureTypeId = tenureType.getOptionId();
                        Intent myIntent;
                        if (tenureTypeId == 99) {
                            myIntent = new Intent(context, PersonListWithDPActivity.class);
                        } else {
                            myIntent = new Intent(context, PersonListActivity.class);
                        }
                        myIntent.putExtra("featureid", featureId);
                        myIntent.putExtra("persontype", "natural");
                        myIntent.putExtra("serverFeaterID", serverFeatureId);
                        startActivity(myIntent);
                    } else if (spinnerPersonType.getSelectedItemPosition() == 2) {
                        Intent myIntent = new Intent(context, AddNonNaturalPersonActivity.class);
                        myIntent.putExtra("featureid", featureId);
                        startActivity(myIntent);
                    }

                }
            }
        });

        propertyInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //flag=db.getFormValues(featureId);
                flag = db.isGeneralAttributeSaved(featureId);
                if (flag) {
                    Intent myIntent = new Intent(context, AddGeneralPropertyActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    startActivity(myIntent);
                } else {
                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });

        tenureInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //	flag=db.getFormValues(featureId);
                flag = db.isGeneralAttributeSaved(featureId);
                if (flag) {
                    Intent myIntent = new Intent(context, AddSocialTenureActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("serverFeaterID", serverFeatureId);
                    startActivity(myIntent);
                } else {

                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });

        multimedia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = db.isGeneralAttributeSaved(featureId);
                if (flag) {
                    Intent myIntent = new Intent(context, MediaListActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    myIntent.putExtra("serverFeaterID", serverFeatureId);
                    startActivity(myIntent);
                } else {
                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });

        custom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                flag = db.isGeneralAttributeSaved(featureId);

                if (flag) {
                    Intent myIntent = new Intent(context, AddCustomAttribActivity.class);
                    myIntent.putExtra("featureid", featureId);
                    startActivity(myIntent);
                } else {
                    String msg = getResources().getString(string.save_genral_attrribute);
                    String warning = getResources().getString(string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveData();
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SimpleDateFormat")
    public void saveData() {
        if (validate()) {
            long hamletId = Long.parseLong(hamletName_Id);//Chagua chaguo

            if (personType.equalsIgnoreCase("Select an option") || personType.equalsIgnoreCase("Chagua chaguo")) {

                msg = getResources().getString(R.string.select_person_type);
                showToast(msg, Toast.LENGTH_LONG);
                //showToast("Please select Person Type",Toast.LENGTH_LONG);
                //spinnerPersonType.setBackgroundColor(context.getResources().getColor(R.color.lightred));
            } else if (hamletId == 0l) {

                //Toast.makeText(getApplicationContext(), "Please select Hamlet",Toast.LENGTH_LONG).show();
                msg = getResources().getString(R.string.Please_select_Hamlet);
                showToast(msg, Toast.LENGTH_LONG);
                //spinnerHamlet.setBackgroundColor(context.getResources().getColor(R.color.lightred));
            } else if (witnessId_1 == 0l) {
                msg = getResources().getString(R.string.Please_select_Witness_1);
                showToast(msg, Toast.LENGTH_LONG);
                //spinnerWitness1.setBackgroundColor(context.getResources().getColor(R.color.lightred));
            } else if (witnessId_2 == 0l) {
                msg = getResources().getString(R.string.Please_select_Witness_2);
                showToast(msg, Toast.LENGTH_LONG);
                //spinnerWitness2.setBackgroundColor(context.getResources().getColor(R.color.lightred));

            } else if (witnessId_1 == witnessId_2) {
                msg = getResources().getString(R.string.Witness_1_and_Witness_2_can_not_be_same);
                showToast(msg, Toast.LENGTH_LONG);

            } else {
                try {
                    if (groupId == 0)
                        groupId = cf.getGroupId();

                    DBController sqllite = new DBController(context);
                    boolean saveResult = sqllite.addPersontype_Hemlet_witness(featureId, personType, hamletName_Id, witness_1, witness_2);

                    if (saveResult) {
						/*Toast toast = Toast.makeText(context,R.string.data_saved, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();*/

                        saveResult = sqllite.saveFormDataTemp(attrList, groupId, featureId, personType);
                        sqllite.close();

                        if (saveResult) {
                            Toast toast = Toast.makeText(context, R.string.data_saved, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            Intent myIntent = new Intent(context, AddGeneralPropertyActivity.class);
                            myIntent.putExtra("featureid", featureId);
                            startActivity(myIntent);
                        } else {
                            Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, R.string.unable_to_save_data, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    cf.appLog("", e);
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(context, R.string.fill_mandatory, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCount() {
        try {

            DBController sqllite = new DBController(context);
            List<Attribute> tmpList = sqllite.getTenureList(featureId, null);
            List<Attribute> tmpList2 = sqllite.getPersonList(featureId);
            List<Attribute> tmpList3 = sqllite.getMediaList(featureId);
            sqllite.close();
            ((TextView) findViewById(R.id.personCount)).setText("" + tmpList2.size());
            //((TextView) findViewById(R.id.tenureCount)).setText(""+tmpList.size());
            ((TextView) findViewById(R.id.multimediaCount)).setText("" + tmpList3.size());


        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        updateCount();
        //flag=db.getFormValues(featureId);
        flag = db.isGeneralAttributeSaved(featureId);
        if (flag) {
            spinnerPersonType.setEnabled(false);
        }
        super.onResume();
    }

    public boolean validate() {
        return GuiUtility.validateAttributes(attrList);
    }

    private void showToast(String message, int length) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}
