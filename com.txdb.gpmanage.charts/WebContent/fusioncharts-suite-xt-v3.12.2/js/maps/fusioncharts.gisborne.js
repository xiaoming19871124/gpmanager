/*
 FusionCharts JavaScript Library
 Copyright FusionCharts Technologies LLP
 License Information at <http://www.fusioncharts.com/license>

 @author FusionCharts Technologies LLP
 @meta package_map_pack
 @id fusionmaps.Gisborne.18.08-08-2012 02:46:09
*/
(function(b){"object"===typeof module&&"undefined"!==typeof module.exports?module.exports=b:b(FusionCharts)})(function(b){b(["private","modules.renderer.js-gisborne",function(){var b=this,c=b.hcLib,d=c.chartAPI,h=c.moduleCmdQueue,c=c.injectModuleDependency,a=!!d.geo,f,g,e;f=[{name:"Gisborne",revision:18,standaloneInit:!0,baseWidth:500,baseHeight:600,baseScaleFactor:10,entities:{1:{outlines:[["M",4225,306,"L",4142,306,4142,211,4230,211,4230,147,4139,147,4139,126,3989,126,3989,97,3884,97,3884,37,3718,
37,3718,100,3620,100,3620,63,3472,63,3472,292,3412,292,"Q",3408,269,3394,259,"L",3292,259,"Q",3292,287,3289,298,"L",3169,298,"Q",3115,298,3109,305,3095,335,3080,331,"L",3080,379,3052,379,3052,412,3085,412,"Q",3077,443,3109,446,"L",3109,682,3083,682,3083,734,"Q",3091,737,3119,737,"L",3119,773,3083,773,3083,802,3049,802,3049,878,"Q",3056,887,3080,883,"L",3080,914,3101,914,3101,967,3115,967,3115,941,"Q",3154,935,3182,893,3210,850,3239,844,"L",3239,818,3260,818,"Q",3243,850,3298,897,3358,950,3358,971,
3358,979,3245,1084,3189,1129,3166,1139,3126,1188,3107,1195,3025,1267,3006,1309,2985,1354,2988,1383,2982,1400,2951,1456,2951,1494,2891,1594,2893,1619,2873,1673,2866,1712,2837,1726,2840,1767,2816,1801,2814,1868,2805,1882,2761,1929,2726,2023,"L",2726,2065,"Q",2723,2068,2700,2083,2697,2090,2697,2122,2701,2132,2679,2159,2677,2182,2664,2198,2643,2214,2630,2230,2631,2254,2612,2305,"L",2612,2343,2588,2343,2588,2398,2559,2398,2559,2364,"Q",2546,2368,2509,2349,2491,2322,2475,2308,2381,2226,2379,2220,2366,2198,
2334,2174,2302,2152,2288,2128,2281,2114,2243,2100,2240,2080,2225,2079,"L",2189,2079,"Q",2184,2041,2175,2037,2158,2001,2151,1990,2135,1963,2105,1963,2067,1967,2045,1963,"L",2039,1932,1994,1932,1994,2023,"Q",2E3,2026,2024,2026,"L",2024,2071,"Q",2018,2082,2018,2092,2018,2103,2029,2106,2036,2107,2054,2110,"L",2054,2149,"Q",2022,2159,2021,2180,2019,2191,2021,2220,1989,2216,1989,2241,1989,2244,2021,2251,2021,2272,2024,2280,"L",2036,2280,2036,2319,"Q",2029,2333,2013,2336,1997,2336,1994,2337,"L",1994,2356,
1946,2356,1946,2377,1911,2377,1911,2439,"Q",1877,2439,1874,2483,1873,2506,1874,2554,"L",1783,2554,"Q",1783,2579,1763,2581,"L",1721,2581,1721,2638,1687,2638,1687,2656,1603,2656,1603,2685,1574,2685,1574,2716,1493,2716,1493,2776,1459,2776,1459,2755,1349,2755,1349,2833,1300,2833,1300,2860,1336,2860,1336,2967,"Q",1308,2979,1306,3018,"L",1303,3079,"Q",1292,3092,1285,3099,1271,3115,1276,3127,1280,3138,1265,3148,1247,3159,1247,3176,1247,3222,1226,3230,1198,3234,1183,3247,1142,3256,1133,3259,1113,3282,1088,
3283,1053,3310,1042,3317,1004,3323,994,3333,940,3360,910,3385,878,3378,866,3401,826,3409,814,3412,807,3442,785,3445,748,3446,746,3475,669,3474,680,3508,"L",637,3508,637,3532,556,3532,553,3560,493,3560,493,3592,451,3592,451,3667,"Q",474,3673,477,3704,478,3719,479,3750,"L",479,3836,"Q",495,3853,508,3860,"L",508,3951,491,3951,"Q",486,3947,455,3947,"L",455,3983,431,3983,431,4010,"Q",384,3998,395,4043,"L",371,4043,371,4017,353,4017,353,3986,332,3986,332,4007,314,4007,314,4032,290,4032,290,4061,257,4061,
257,4116,230,4116,230,4155,199,4155,199,4194,176,4194,176,4232,121,4232,118,4200,58,4200,58,4223,40,4223,40,4239,"Q",50,4241,76,4239,97,4240,89,4265,"L",131,4265,"Q",134,4275,134,4289,"L",160,4289,"Q",209,4329,283,4363,314,4381,335,4399,370,4429,395,4430,416,4431,442,4472,458,4483,500,4501,509,4511,529,4511,548,4510,557,4528,"L",641,4528,641,4559,653,4559,653,4366,670,4366,670,4310,691,4310,691,4339,"Q",702,4343,767,4392,811,4426,860,4423,896,4420,931,4423,937,4423,959,4454,966,4464,998,4468,1015,
4483,1047,4483,"L",1130,4480,1130,4511,1106,4511,1106,4559,1079,4559,1079,4621,1015,4621,1015,4679,983,4679,983,4733,1028,4733,1028,4754,1072,4754,1072,4792,1145,4792,1145,4859,"Q",1156,4862,1187,4862,"L",1187,4930,1148,4930,1148,5011,1211,5011,"Q",1174,5078,1292,5068,1295,5078,1295,5099,"L",1325,5099,1325,5129,1349,5129,1349,5144,"Q",1343,5156,1343,5214,1343,5258,1349,5267,"L",1370,5267,1370,5243,"Q",1412,5251,1399,5215,"L",1574,5215,"Q",1574,5222,1577,5222,"L",1577,5278,"Q",1553,5278,1540,5282,
"L",1540,5363,1574,5363,1575,5411,1616,5411,1616,5380,1656,5380,1656,5423,1651,5458,1651,5483,1673,5483,1673,5509,"Q",1670,5514,1670,5528,"L",1687,5528,1687,5468,1707,5468,1707,5444,1728,5444,1728,5411,1770,5411,1770,5514,1733,5535,1733,5661,1764,5661,1764,5627,1827,5627,1827,5654,1911,5654,1911,5629,1957,5627,1957,5654,"Q",1954,5670,1954,5698,"L",1990,5698,1990,5655,2021,5655,2021,5627,2057,5627,2057,5654,2078,5655,"Q",2081,5666,2080,5685,2081,5701,2095,5714,"L",2122,5714,2122,5687,2161,5687,2161,
5634,2221,5634,"Q",2222,5656,2225,5708,2226,5710,2247,5715,2265,5719,2268,5739,"L",2282,5742,"Q",2289,5742,2292,5715,"L",2334,5715,2334,5749,2359,5749,2359,5795,2358,5798,2341,5798,"Q",2338,5795,2328,5795,"L",2328,5855,2299,5855,2299,5893,"Q",2321,5898,2359,5908,2356,5932,2384,5933,2399,5933,2425,5929,2427,5941,2427,5965,"L",2489,5965,2489,5953,2522,5956,2522,5890,"Q",2514,5845,2515,5821,2515,5779,2562,5789,"L",2562,5598,2590,5598,"Q",2588,5564,2587,5550,2587,5525,2613,5535,"L",2613,5514,"Q",2588,
5513,2584,5509,2582,5507,2583,5486,"L",2587,5348,2626,5348,2626,5327,2606,5327,2606,5264,2636,5264,2636,5204,2662,5204,"Q",2649,5173,2696,5180,"L",2696,5154,2719,5154,2719,5120,"Q",2733,5120,2739,5123,"L",2757,5123,2757,5096,2809,5096,2809,5074,"Q",2730,5071,2693,5070,"L",2693,5053,"Q",2693,4920,2696,4894,2722,4888,2714,4870,2714,4867,2719,4834,2779,4845,2761,4796,"L",2792,4796,"Q",2795,4786,2795,4757,"L",2842,4757,2861,4763,2879,4763,"Q",2880,4747,2883,4736,"L",2999,4736,2999,4771,3017,4771,3017,
4807,3059,4807,3059,4877,"Q",3101,4877,3106,4873,"L",3109,4873,"Q",3106,4880,3106,4898,"L",3148,4898,3148,4807,3166,4807,3166,4771,3215,4771,"Q",3211,4748,3250,4732,3256,4729,3259,4707,3261,4693,3284,4702,"L",3284,4676,3383,4676,3383,4630,3425,4630,"Q",3425,4603,3428,4591,3502,4596,3498,4535,3495,4504,3541,4507,3573,4509,3584,4503,"L",3584,4433,3611,4433,3611,4399,3647,4399,3647,4376,3692,4376,"Q",3671,4332,3710,4321,3721,4318,3780,4318,"L",3780,4271,3809,4271,3809,4225,3858,4225,"Q",3854,4205,3887,
4208,3887,4195,3890,4190,"L",3890,4153,"Q",3866,4156,3866,4117,3866,4089,3868,4088,3870,4084,3896,4078,"L",3896,4007,3864,4007,"Q",3863,4003,3861,4003,"L",3861,3958,3887,3958,3887,3919,3924,3919,3924,3866,3960,3866,"Q",3960,3838,3963,3824,3968,3822,4005,3793,4030,3773,4067,3785,"L",4067,3754,4109,3754,4109,3727,"Q",4085,3730,4088,3694,"L",4055,3694,4055,3670,"Q",4058,3662,4058,3644,4081,3646,4097,3637,4105,3631,4122,3620,"L",4122,3556,4083,3556,4083,3592,3977,3592,3977,3553,3963,3553,"Q",3968,3525,
3968,3508,"L",4016,3508,4016,3475,4062,3475,4062,3391,4086,3391,4086,3346,4067,3346,4067,3254,4130,3254,4130,3229,4167,3229,4167,3202,4086,3202,4086,3190,"Q",4099,3163,4098,3121,"L",4098,3046,4128,3046,4128,3019,4034,3019,4034,2944,4059,2944,4059,2914,4122,2914,4122,2803,4151,2803,"Q",4136,2773,4162,2769,4178,2766,4209,2768,"L",4209,2722,4161,2722,"Q",4167,2696,4125,2677,4120,2674,4091,2632,"L",4091,2494,4130,2494,4130,2467,4206,2467,4206,2446,"Q",4224,2446,4260,2444,"L",4260,2413,4281,2413,4281,
2298,"Q",4250,2286,4259,2272,"L",4259,2220,4230,2220,4230,2185,4196,2185,"Q",4199,2167,4175,2157,"L",4175,2034,4199,2034,4199,1974,4238,1974,4238,1945,4285,1945,"Q",4278,1922,4305,1906,4330,1889,4316,1854,"L",4379,1854,4379,1804,4406,1804,4406,1765,4373,1765,4373,1650,4400,1650,4400,1600,4433,1600,4433,1456,4454,1456,4454,1425,"Q",4463,1424,4481,1425,4492,1424,4487,1402,4514,1409,4533,1383,4552,1357,4589,1354,4591,1308,4589,1239,"L",4625,1239,"Q",4622,1235,4625,1203,4628,1171,4670,1168,"L",4670,1129,
4711,1129,4711,1077,4737,1077,"Q",4735,1055,4737,1047,4741,1030,4769,1033,4763,1021,4787,1009,4797,1002,4802,982,4839,952,4850,943,4880,920,4878,886,4875,862,4885,857,4896,851,4896,829,"L",4931,829,4931,795,"Q",4961,810,4962,742,4961,718,4959,716,4956,714,4925,711,"L",4925,682,4868,682,"Q",4861,647,4811,628,"L",4811,601,"Q",4739,604,4760,552,"L",4734,552,4734,513,"Q",4683,520,4651,520,4591,519,4583,481,"L",4478,481,4478,457,4306,457,"Q",4305,426,4262,392,"Q",4222,359,4225,306,"Z"]],label:"Gisborne District",
shortLabel:"GB",labelPosition:[250.1,342.1],labelAlignment:["center","middle"]}}}];e=f.length;if(a)for(;e--;)a=f[e],d(a.name.toLowerCase(),a,d.geo);else for(;e--;)a=f[e],g=a.name.toLowerCase(),c("maps",g,1),h.maps.unshift({cmd:"_call",obj:window,args:[function(a,c){d.geo?d(a,c,d.geo):b.raiseError(b.core,"12052314141","run","JavaScriptRenderer~Maps._call()",Error("fusioncharts.maps.js is required in order to define vizualization"))},[g,a],window]})}])});
