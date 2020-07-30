/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { useState, useEffect } from "react";
import { View, Text, TouchableOpacity, Alert,DeviceEventEmitter, NativeEventEmitter, NativeModules } from "react-native";



import recordTek  from "./ToastExample";
import { eventName } from './constantEvent'
const App: () => React$Node = () => {
  const [minu, setMinus] = useState("abc");
  const [isDetect , setIsDetect] = useState(false)
  useEffect( () => {
    const eventRecord = new  NativeEventEmitter (NativeModules.recordTek)
    recordTek.startDetect()
    eventRecord.addListener("RECORD", (event:string) => console.warn(event))
    eventRecord.addListener("NOT_CONNECT", (event:any) => console.warn(event))
    // eventRecord.addListener("STOP_DETECT", (event:boolean) => {
    //   if  (event) {
    //     recordTek.startDetect()
    //   }
    // } )
    return () =>{
      recordTek.stopDetect()
      eventRecord.removeSubscription(NativeModules.recordTek)
    }
    recordTek.test()
    eventRecord.addListener("test",event=>{console.warn(event)})
  }, []);
  const detetch = async () => {
    let A : string  = await recordTek.detetchCard()
    console.warn(A)

  }

  return (
    <View>
      <TouchableOpacity onPress={detetch} > 
        <Text> Click me </Text>
        <Text></Text>
        </TouchableOpacity> 
    </View>
  );
};

export default App;
