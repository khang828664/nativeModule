/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { useState, useEffect } from "react";
import { View, Text, TouchableOpacity, Alert,DeviceEventEmitter } from "react-native";



import recordTek  from "./ToastExample";
import { eventName } from './constantEvent'
const App: () => React$Node = () => {
  const [minu, setMinus] = useState("abc");
  const [isDetect , setIsDetect] = useState(false)
  useEffect( () => {
    DeviceEventEmitter.addListener("RECORD_TEKMEDI_CARD",event=>{

      console.log(event)
    })
    //  async function startRecord1 () {
    //     try {
    //       let A = await recordTek.getTekmediCard()
    //       console.warn('asas')
    //       setMinus(A)
    //       setIsDetect(true)
    //      } catch (e) {
    //        console.warn(e)
    //      }
    //   }
    //   setInterval(() =>startRecord1(), 3000 )
      
  }, []);
  const resetDetect = () => {
    setMinus("")
    setIsDetect(false)
  }

  return (
    <View>
     
        <Text> Click me </Text>
        <Text></Text>
    </View>
  );
};

export default App;
