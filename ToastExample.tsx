import { NativeModules } from "react-native";
type recordTekType = {
    startDetect() : any, 
    stopDetect() : any,    
    test (): any 
    detetchCard () : Promise<string>
}
export default  NativeModules.recordTek  as recordTekType ; 
