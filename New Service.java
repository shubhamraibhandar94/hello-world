//New Service
global class Casedetails
{
	Webservice String CaseNumber;
	Webservice String Description;
	Webservice String Status;
	Webservice String Substatus;
	Webservice String Owner;
	Webservice DateTime DateOpened;
	Webservice DateTime LastTouch;
	Webservice String CaseAge;
	Webservice String Severity;
	Webservice String Product;
	Webservice List<String> Collectors;
}

//New Service - Response for Customer Search
global class ResponseforCustomerSearch
{   
	webservice list<CaseDetails> CaseList; 	
    webservice String status;
    webservice String message;    
}


//New Service - Serach from Account's Customer Id - Start
webservice static ResponseforCustomerSearch readCasesfromCustomerService(List<String> customerlist)
{       
    ResponseforCustomerSearch respObj = new ResponseforCustomerSearch();
    respObj.status=SUCCESS;
    respObj.message=BLANK;          
    respObj.CaseList = new List<CaseDetails>();    
    
	List<Casedetails> casdetlist = new List<Casedetails>();
	Casedetails csdet = new Casedetails();
	csdet.Collectors = new List<String>();
    	
    Set<String> CustomerSet;
        
    if(customerlist != null && !customerlist.isEmpty())    
    {   
        CustomerSet = new Set<String>(customerlist);  
    }
    else
    {
        respObj.status = ERROR;
        respObj.message = EMPTY_INPUT_LIST;
        return respObj;
    }

	Map<String, list<Case_Extension__c>> casmap = new Map<String, list<Case_Extension__c>>();	
	
    try
    {
        if(CustomerSet != null && !CustomerSet.isEmpty())
        {
            casmap = SkylineCollectorUtility.readCasefromCustomerNumber(CustomerSet); 
        }        
        
        if(casmap.isEmpty())
        {
            respObj.status = ERROR;
            respObj.message = 'Customer Id is Invalid OR Doesn\'t Exist';
            return respObj;
        }      
        
        List<String> allcollid = new List<String>();

        for(String custno : casmap.keySet())
        {                       
            for(Case_Extension__c cc : casmap.get(custno))
            {
                allcollid = new List<String>();
                if(cc.Skyline_Collector_ID__c!= null && cc.Skyline_Collector_ID__c.contains(','))
				{
					allcollid = cc.Skyline_Collector_ID__c.split(',');
                }
				else
				{
					allcollid.add(cc.Skyline_Collector_ID__c);
                }
			  
				csdet = new Casedetails();				
				csdet.CaseNumber = Case__r.CaseNumber;
				csdet.Description = Case__r.Description;
				csdet.Status Case__r.Status;
				csdet.Substatus = Case__r.Sub_Status__c
				csdet.Owner = Case__r.Owner.Name;
				csdet.DateOpened = Case__r.CreatedDate;
				csdet.LastTouch = Case__r.GSS_Last_Touch_Time__c;			
				csdet.CaseAge = Case__r.Case_Age_Hours__c;
				csdet.Severity; = Case__r.Severity__c;
				csdet.Product = Case__r.Product.Name;
				csdet.Collectors = allcollid;
                casdetlist.add(csdet);
            }                                    
        }
		     
        if(casdetlist != null && !casdetlist.isEmpty() && casdetlist.size()>0)
        {               
			respObj.status=SUCCESS;
			respObj.message=BLANK;          
			respObj.CaseList = casdetlist;  
        }
	}
	catch(Exception ex){
        respObj.status=ERROR;
        respObj.message=ERROR+COLON_SPACE+ex.getMessage();        
        CreateApexErrorLog.insertHandledExceptions(ex, null, null, null, 'ApexClass', 'SkylineCollectorService', 'readCasesfromCustomerService');
        return respObj;
    }    
    return respObj;
}
//New Service - Serach from Account's Customer Id - Start