package com.mgroup.turnonoffusbrelay;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    UsbDevice[] UsbRelayModules = null;
    Spinner spRelayModules;
    Switch swRelay1, swRelay2, swRelay3, swRelay4, swRelay5, swRelay6, swRelay7, swRelay8;
    Switch swRelayAllOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swRelay1 = (Switch) findViewById(R.id.swRelay1);
        swRelay2 = (Switch) findViewById(R.id.swRelay2);
        swRelay3 = (Switch) findViewById(R.id.swRelay3);
        swRelay4 = (Switch) findViewById(R.id.swRelay4);
        swRelay5 = (Switch) findViewById(R.id.swRelay5);
        swRelay6 = (Switch) findViewById(R.id.swRelay6);
        swRelay7 = (Switch) findViewById(R.id.swRelay7);
        swRelay8 = (Switch) findViewById(R.id.swRelay8);
        spRelayModules = (Spinner) findViewById(R.id.spRelays);

        swRelayAllOnOff = (Switch) findViewById(R.id.swRelayAllOnOff);

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh);

        refreshUsbPorts(spRelayModules);

        swRelay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], 1, swRelay1.isChecked());
            }
        });
        swRelay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], 2, swRelay2.isChecked());
            }
        });

        swRelay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], 3, swRelay3.isChecked());
            }
        });

        swRelay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], 4, swRelay4.isChecked());
            }
        });

        swRelay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], 5, swRelay5.isChecked());
            }
        });

        swRelay6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], 6, swRelay6.isChecked());
            }
        });

        swRelay7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], 7, swRelay7.isChecked());
            }
        });

        swRelay8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], 8, swRelay8.isChecked());
            }
        });

        swRelayAllOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=1; i<= 8; i++){
                    turnOnOffRelay(UsbRelayModules[spRelayModules.getSelectedItemPosition()], i, swRelayAllOnOff.isChecked());
                }
            }
        });


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshUsbPorts(spRelayModules);
            }
        });
    }


    private void refreshUsbPorts(Spinner spRelayModules){
        if (UsbRelayModules != null)
            UsbRelayModules = null;
        try {
            UsbRelayModules = getUsbRelaysArray();
            if ((UsbRelayModules != null))
            {
                List<String> relaysList = new ArrayList<>();
                for (UsbDevice usbHidDev:UsbRelayModules) {
                    relaysList.add(String.valueOf(usbHidDev.getDeviceId()));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, relaysList);
                spRelayModules.setAdapter(adapter);
                if (adapter.getCount() > 0) {
                    spRelayModules.setSelection(0);
                    swRelay1.setEnabled(true);
                    swRelay2.setEnabled(true);
                    swRelay3.setEnabled(true);
                    swRelay4.setEnabled(true);
                    swRelay5.setEnabled(true);
                    swRelay6.setEnabled(true);
                    swRelay7.setEnabled(true);
                    swRelay8.setEnabled(true);
                    swRelayAllOnOff.setEnabled(true);
                }
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error: " + e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
        }

        if (UsbRelayModules == null || UsbRelayModules.length == 0)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"None"});
            spRelayModules.setAdapter(adapter);
            swRelay1.setEnabled(false);
            swRelay2.setEnabled(false);
            swRelay3.setEnabled(false);
            swRelay4.setEnabled(false);
            swRelay5.setEnabled(false);
            swRelay6.setEnabled(false);
            swRelay7.setEnabled(false);
            swRelay8.setEnabled(false);
            swRelayAllOnOff.setEnabled(false);
        }
    }

    private void turnOnOffRelay(UsbDevice device, int relayNum, boolean onOff) {
        try {
            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection connection = manager.openDevice(device);
            if (connection == null || !connection.claimInterface(device.getInterface(0), true)) {
                Toast.makeText(MainActivity.this, "No connection to this device (1)", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] data = new byte[8];
            Arrays.fill(data, (byte) 0);
            data[0] = onOff ? (byte) (255) : (byte) (253);
            data[1] = (byte) relayNum;
            sendCommand(data, 0, data.length, 20, connection, device.getInterface(0).getEndpoint(0));
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private  void turnOnOffUsbRelay8Channels(){

    }

    public void sendCommand(byte[] data, int offset, int size, int timeout, UsbDeviceConnection connection, UsbEndpoint endPoint) {
        if (offset != 0) {
            data = Arrays.copyOfRange(data, offset, size);
        }
        if (endPoint == null) {
            Toast.makeText(MainActivity.this, "Error: " + "command not executed!", Toast.LENGTH_SHORT).show();
        } else {
            connection.controlTransfer(0x21, 0x09, 0x0300, 0x00, data, size, timeout);
        }
    }

    private UsbDevice[] getUsbRelaysArray() {
        List<UsbDevice> relays = null;
        UsbManager manager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
        if (manager == null)
            return null;
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (relays == null)
                relays = new ArrayList<>();
            if (device.getVendorId() == 0x16C0 && device.getProductId() == 0x05DF)
                relays.add(device);
        }

        if (relays == null || relays.size() == 0)
            return null;
        else
            return relays.toArray(new UsbDevice[relays.size()]);
    }
}
