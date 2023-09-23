/*
 * Copyright (c) 2020. Julian Steenbakker.
 * All rights reserved. Use of this source code is governed by a
 * BSD-style license that can be found in the LICENSE file.
 */

// ignore: unnecessary_import
import 'dart:math';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_ble_peripheral/flutter_ble_peripheral.dart';

void main() => runApp(const FlutterBlePeripheralExample());

class FlutterBlePeripheralExample extends StatefulWidget {
  const FlutterBlePeripheralExample({Key? key}) : super(key: key);

  @override
  FlutterBlePeripheralExampleState createState() =>
      FlutterBlePeripheralExampleState();
}

class FlutterBlePeripheralExampleState extends State<FlutterBlePeripheralExample> {
  final methodChannel = const MethodChannel('com.tdl/ble');
  Random random = Random();

  static final List<Uint8List> gears = [
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -59, 23, 92]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -12, 29, 124]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -9, -122, 78]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -10, 15, 95]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -15, -80, 43]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -16, 57, 58]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -13, -94, 8]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -14, 43, 25]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -3, -36, -31]),
    Uint8List.fromList([109, -74, 67, -50, -105, -2, 66, 124, -4, 85, -16]),
  ];

  final AdvertiseData advertiseData = AdvertiseData(
    serviceUuid: '0000ae8f-0000-1000-8000-00805f9b34fb',
    manufacturerId: 255,
    manufacturerData: Uint8List.fromList([0x77, 0x62, 0x4d, 0x53, 0x45, 0x11, 0x00, 0x00, 0x00, 0x00, 0x00]),
    //manufacturerData: Uint8List.fromList(BleSoUtil.getRFPayload([0x77, 0x62, 0x4d, 0x53, 0x45, 0x11, 0x00, 0x00, 0x00, 0x00, 0x00], [0x77, 0x62, 0x4d, 0x53, 0x45, 0x11, 0x00, 0x00, 0x00, 0x00, 0x00], 11, [])),
  );

  final AdvertiseSettings advertiseSettings = AdvertiseSettings(
    advertiseMode: AdvertiseMode.advertiseModeBalanced,
    txPowerLevel: AdvertiseTxPower.advertiseTxPowerMedium,
    timeout: 2500,
    connectable: true,
  );

  final AdvertiseSetParameters advertiseSetParameters = AdvertiseSetParameters();

  bool _isSupported = false;

  @override
   initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> send(int gear) async {
    //[109, 182, 67, 206, 151, 254, 66, 124, 244, 29, 124]
    //[0x6D, 0xB6, 0x43, 0xCE, 0x97, 0xFE, 0x42, 0x7C, 0xF4, 0x1D, 0x7C]
    /*Uint8List? buffer = (await getBleCommand(11)) as Uint8List?;
    debugPrint("buffer: $buffer");*/

    //1.原生Java方式
    //sendCmd(gear);
    //return;

    //2.Dart方式
    Uint8List buffer = gears[gear];
    buffer = Uint8List.fromList([0x6D, 0xB6, 0x43, 0xCF, 0x7E, 0x8F, 0x47, 0x11, 0xB0, 0xFA, 0xAC]);
    buffer = Uint8List.fromList([0x08, 0xF9, 0x22, 0x49, 0xBA, 0x47, 0xBC, 0xC4, 0xFE, 0xEB, 0x0B]);
    final AdvertiseData advertiseData = createAdvertiseData(65520, buffer);//255
    if (await FlutterBlePeripheral().isAdvertising) {
      await FlutterBlePeripheral().stop();
    }
    await FlutterBlePeripheral().start(advertiseData: advertiseData, advertiseSettings: advertiseSettings);
    await Future.delayed(const Duration(milliseconds: 1000), (){});
    await FlutterBlePeripheral().stop();
  }

  AdvertiseData createAdvertiseData(int manufacturerId, Uint8List? manufacturerData) {
    return AdvertiseData(
      serviceUuid: '0000ae8f-0000-1000-8000-00805f9b34fb',
      manufacturerId: manufacturerId,
      manufacturerData: manufacturerData,);
  }

  Future<List<int>?> getBleCommand(int mode) async {
    return await methodChannel.invokeMethod<List<int>>('getBleCommand', mode);
  }

  Future<void> sendCmd(int mode) async {
    await methodChannel.invokeMethod<String>('sendCmd', mode);
  }

  Future<void> initPlatformState() async {
    final isSupported = await FlutterBlePeripheral().isSupported;
    setState(() {
      _isSupported = isSupported;
    });
  }

  Future<void> _toggleAdvertise() async {
    if (await FlutterBlePeripheral().isAdvertising) {
      await FlutterBlePeripheral().stop();
    } else {
      await FlutterBlePeripheral().start(advertiseData: advertiseData);
    }
  }

  Future<void> _toggleAdvertiseSet() async {
    if (await FlutterBlePeripheral().isAdvertising) {
      await FlutterBlePeripheral().stop();
    } else {
      await FlutterBlePeripheral().start(
        advertiseData: advertiseData,
        advertiseSetParameters: advertiseSetParameters,
      );
    }
  }

  Future<void> _requestPermissions() async {
    final hasPermission = await FlutterBlePeripheral().hasPermission();
    switch (hasPermission) {
      case BluetoothPeripheralState.denied:
        _messangerKey.currentState?.showSnackBar(
          const SnackBar(
            backgroundColor: Colors.red,
            content: Text(
              "We don't have permissions, requesting now!",
            ),
          ),
        );

        await _requestPermissions();
        break;
      default:
        _messangerKey.currentState?.showSnackBar(
          SnackBar(
            backgroundColor: Colors.green,
            content: Text(
              'State: $hasPermission!',
            ),
          ),
        );
        break;
    }
  }

  Future<void> _hasPermissions() async {
    final hasPermissions = await FlutterBlePeripheral().hasPermission();
    _messangerKey.currentState?.showSnackBar(
      SnackBar(
        content: Text('Has permission: $hasPermissions'),
        backgroundColor: hasPermissions == BluetoothPeripheralState.granted
            ? Colors.green
            : Colors.red,
      ),
    );
  }

  final _messangerKey = GlobalKey<ScaffoldMessengerState>();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      scaffoldMessengerKey: _messangerKey,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter BLE Peripheral'),
        ),
        body: Center(
          child:
          SingleChildScrollView(
            child:
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text('Is supported: $_isSupported'),
              StreamBuilder(
                stream: FlutterBlePeripheral().onPeripheralStateChanged,
                initialData: PeripheralState.unknown,
                builder:
                    (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
                  return Text(
                    'State: ${describeEnum(snapshot.data as PeripheralState)}',
                  );
                },
              ),
              // StreamBuilder(
              //     stream: FlutterBlePeripheral().getDataReceived(),
              //     initialData: 'None',
              //     builder:
              //         (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              //       return Text('Data received: ${snapshot.data}');
              //     },),
              Text('Current UUID: ${advertiseData.serviceUuid}'),
              MaterialButton(
                onPressed: () {
                  final int gear = random.nextInt(10);
                  send(gear);
                },
                child: Text(
                  'Toggle advertising',
                  style: Theme.of(context)
                      .primaryTextTheme
                      .labelLarge!
                      .copyWith(color: Colors.blue),
                ),
              ),
              MaterialButton(
                onPressed: () async {
                  await FlutterBlePeripheral().start(
                    advertiseData: advertiseData,
                    advertiseSetParameters: advertiseSetParameters,
                  );
                },
                child: Text(
                  'Start advertising',
                  style: Theme.of(context)
                      .primaryTextTheme
                      .labelLarge!
                      .copyWith(color: Colors.blue),
                ),
              ),
              MaterialButton(
                onPressed: () async {
                  await FlutterBlePeripheral().stop();
                },
                child: Text(
                  'Stop advertising',
                  style: Theme.of(context)
                      .primaryTextTheme
                      .labelLarge!
                      .copyWith(color: Colors.blue),
                ),
              ),
              MaterialButton(
                onPressed: _toggleAdvertiseSet,
                child: Text(
                  'Toggle advertising set for 1 second',
                  style: Theme.of(context)
                      .primaryTextTheme
                      .labelLarge!
                      .copyWith(color: Colors.blue),
                ),
              ),
              StreamBuilder(
                stream: FlutterBlePeripheral().onPeripheralStateChanged,
                initialData: PeripheralState.unknown,
                builder: (
                  BuildContext context,
                  AsyncSnapshot<PeripheralState> snapshot,
                ) {
                  return MaterialButton(
                    onPressed: () async {
                      final bool enabled = await FlutterBlePeripheral()
                          .enableBluetooth(askUser: false);
                      if (enabled) {
                        _messangerKey.currentState!.showSnackBar(
                          const SnackBar(
                            content: Text('Bluetooth enabled!'),
                            backgroundColor: Colors.green,
                          ),
                        );
                      } else {
                        _messangerKey.currentState!.showSnackBar(
                          const SnackBar(
                            content: Text('Bluetooth not enabled!'),
                            backgroundColor: Colors.red,
                          ),
                        );
                      }
                    },
                    child: Text(
                      'Enable Bluetooth (ANDROID)',
                      style: Theme.of(context)
                          .primaryTextTheme
                          .labelLarge!
                          .copyWith(color: Colors.blue),
                    ),
                  );
                },
              ),
              MaterialButton(
                onPressed: () async {
                  final bool enabled =
                      await FlutterBlePeripheral().enableBluetooth();
                  if (enabled) {
                    _messangerKey.currentState!.showSnackBar(
                      const SnackBar(
                        content: Text('Bluetooth enabled!'),
                        backgroundColor: Colors.green,
                      ),
                    );
                  } else {
                    _messangerKey.currentState!.showSnackBar(
                      const SnackBar(
                        content: Text('Bluetooth not enabled!'),
                        backgroundColor: Colors.red,
                      ),
                    );
                  }
                },
                child: Text(
                  'Ask if enable Bluetooth (ANDROID)',
                  style: Theme.of(context)
                      .primaryTextTheme
                      .labelLarge!
                      .copyWith(color: Colors.blue),
                ),
              ),
              MaterialButton(
                onPressed: _requestPermissions,
                child: Text(
                  'Request Permissions',
                  style: Theme.of(context)
                      .primaryTextTheme
                      .labelLarge!
                      .copyWith(color: Colors.blue),
                ),
              ),
              MaterialButton(
                onPressed: _hasPermissions,
                child: Text(
                  'Has permissions',
                  style: Theme.of(context)
                      .primaryTextTheme
                      .labelLarge!
                      .copyWith(color: Colors.blue),
                ),
              ),
              MaterialButton(
                onPressed: () => FlutterBlePeripheral().openBluetoothSettings(),
                child: Text(
                  'Open bluetooth settings',
                  style: Theme.of(context)
                      .primaryTextTheme
                      .labelLarge!
                      .copyWith(color: Colors.blue),
                ),
              ),
            ],
          ),),
        ),
      ),
    );
  }
}
